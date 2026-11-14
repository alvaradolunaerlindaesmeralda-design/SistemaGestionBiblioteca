package servicio;

import com.mongodb.client.model.Filters;
import dao.PrestamoDAO;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import modelo.Prestamo;
import modelo.Validador;
import org.bson.Document;
import org.bson.conversions.Bson;


public class PrestamoServicio {

    public static final String EXITO = "EXITO";
    private static final String MSG_MONGO = "Error de conexión, lectura o escritura en MongoDB: ";

    private final PrestamoDAO dao = new PrestamoDAO();


    // Registrar préstamo//
   

    /**
     * Valida y registra un préstamo. Las fechas deben venir como AAAA-MM-DD.
     * Si todo sale bien devuelve "EXITO:" seguido del código generado.
     */
    public String registrarPrestamo(String codigoUsuario, String codigoMaterial,
            String fechaPrestamoTxt, String fechaPrevistaTxt) {

        if (Validador.esVacio(codigoUsuario) || Validador.esVacio(codigoMaterial)
                || Validador.esVacio(fechaPrestamoTxt) || Validador.esVacio(fechaPrevistaTxt)) {
            return "Todos los campos son obligatorios.";
        }

        LocalDate fechaPrestamo = parsearFecha(fechaPrestamoTxt);
        if (fechaPrestamo == null) {
            return "La fecha del préstamo no es válida. Use el formato AAAA-MM-DD (ejemplo: 2026-11-13).";
        }
        LocalDate fechaPrevista = parsearFecha(fechaPrevistaTxt);
        if (fechaPrevista == null) {
            return "La fecha prevista de devolución no es válida. Use el formato AAAA-MM-DD (ejemplo: 2026-11-20).";
        }
        if (fechaPrestamo.isAfter(LocalDate.now())) {
            return "La fecha del préstamo no puede ser futura.";
        }
        if (fechaPrevista.isBefore(fechaPrestamo)) {
            return "La fecha prevista de devolución no puede ser anterior a la fecha del préstamo.";
        }

        String usuarioCod = codigoUsuario.trim();
        String materialCod = codigoMaterial.trim();

        try {
            Document usuario = dao.buscarUsuario(usuarioCod);
            if (usuario == null) {
                return "El usuario " + usuarioCod + " no existe.";
            }
            if (!usuario.getBoolean("activo", true)) {
                return "El usuario " + usuarioCod + " está inactivo y no puede solicitar préstamos.";
            }

            Document material = dao.buscarMaterial(materialCod);
            if (material == null) {
                return "El material " + materialCod + " no existe.";
            }

            // Descuenta un ejemplar de forma atómica; si no quedan, rechaza el préstamo.
            if (!dao.tomarEjemplar(materialCod)) {
                return "El material " + materialCod + " no está disponible: no quedan ejemplares.";
            }

            String codigo = generarCodigo();
            try {
                dao.guardar(new Prestamo(codigo, usuarioCod, materialCod, fechaPrestamo, fechaPrevista));
            } catch (Exception e) {
                // Si no se pudo guardar el préstamo se devuelve el ejemplar para no dejar datos inconsistentes.
                dao.devolverEjemplar(materialCod);
                return MSG_MONGO + e.getMessage();
            }
            return EXITO + ":" + codigo;
        } catch (Exception e) {
            return MSG_MONGO + e.getMessage();
        }
    }

    // Registrar devolución//
    

 
    public String registrarDevolucion(String codigoPrestamo) {
        if (Validador.esVacio(codigoPrestamo)) {
            return "Seleccione el préstamo que desea devolver.";
        }
        String mensajeYaDevuelto = "Este préstamo ya fue devuelto; no se puede registrar una segunda devolución.";
        try {
            Prestamo prestamo = dao.buscarPorCodigo(codigoPrestamo.trim());
            if (prestamo == null) {
                return "El préstamo " + codigoPrestamo.trim() + " no existe.";
            }
            if (Prestamo.DEVUELTO.equals(prestamo.getEstado())) {
                return mensajeYaDevuelto;
            }

            LocalDate hoy = LocalDate.now();
            if (!dao.marcarDevuelto(prestamo.getCodigo(), hoy)) {
                return mensajeYaDevuelto;
            }
            try {
                dao.devolverEjemplar(prestamo.getCodigoMaterial());
            } catch (Exception e) {
                // Si no se pudo liberar el material se deshace la devolución para mantener la consistencia.
                dao.reabrir(prestamo.getCodigo());
                return "No se pudo actualizar la disponibilidad del material, por lo que la devolución no se registró. "
                        + MSG_MONGO + e.getMessage();
            }

            int retraso = prestamo.diasRetraso(hoy);
            return EXITO + ":" + (retraso > 0 ? "con " + retraso + " día(s) de retraso" : "a tiempo");
        } catch (Exception e) {
            return MSG_MONGO + e.getMessage();
        }
    }

    public List<Prestamo> buscarPrestamos(String codigo, String codigoUsuario, String codigoMaterial, String estado) {
        List<Bson> filtros = new ArrayList<>();
        agregarFiltroParcial(filtros, "codigo", codigo);
        agregarFiltroParcial(filtros, "codigoUsuario", codigoUsuario);
        agregarFiltroParcial(filtros, "codigoMaterial", codigoMaterial);
        if (!Validador.esVacio(estado) && !"Todos".equalsIgnoreCase(estado.trim())) {
            filtros.add(Filters.eq("estado", estado.trim()));
        }
        return listar(filtros.isEmpty() ? null : Filters.and(filtros));
    }

    public List<Prestamo> listarActivos() {
        return listar(Filters.eq("estado", Prestamo.PRESTADO));
    }

    public List<Prestamo> listarVencidos() {
        return listar(Filters.and(
                Filters.eq("estado", Prestamo.PRESTADO),
                Filters.lt("fechaPrevista", LocalDate.now().toString())));
    }

    /** Todos los préstamos, activos y finalizados. */
    public List<Prestamo> listarHistorial() {
        return listar(null);
    }


    public List<String[]> opcionesUsuarios() {
        List<String[]> opciones = new ArrayList<>();
        try {
            for (Document u : dao.listarUsuariosActivos()) {
                String codigo = u.getString("codigo");
                opciones.add(new String[]{codigo, codigo + " - " + u.getString("nombreCompleto")});
            }
        } catch (Exception e) {
            throw new RuntimeException(MSG_MONGO + e.getMessage(), e);
        }
        return opciones;
    }

    /** Solo materiales con al menos un ejemplar disponible. */
    public List<String[]> opcionesMateriales() {
        List<String[]> opciones = new ArrayList<>();
        try {
            for (Document m : dao.listarMateriales()) {
                int disponibles = PrestamoDAO.cantidadDisponible(m);
                if (disponibles > 0) {
                    String codigo = m.getString("codigo");
                    opciones.add(new String[]{codigo,
                        codigo + " - " + m.getString("titulo") + " (disponibles: " + disponibles + ")"});
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(MSG_MONGO + e.getMessage(), e);
        }
        return opciones;
    }

    public List<String[]> opcionesPrestamosActivos() {
        List<String[]> opciones = new ArrayList<>();
        for (Prestamo p : listarActivos()) {
            opciones.add(new String[]{p.getCodigo(),
                p.getCodigo() + " | usuario " + p.getCodigoUsuario()
                + " | material " + p.getCodigoMaterial()
                + " | prevista " + p.getFechaPrevista()});
        }
        return opciones;
    }


    private List<Prestamo> listar(Bson filtro) {
        try {
            return dao.listar(filtro);
        } catch (Exception e) {
            throw new RuntimeException(MSG_MONGO + e.getMessage(), e);
        }
    }

    private void agregarFiltroParcial(List<Bson> filtros, String campo, String texto) {
        if (!Validador.esVacio(texto)) {
            filtros.add(Filters.regex(campo, Pattern.quote(texto.trim()), "i"));
        }
    }

    private String generarCodigo() {
        long numero = dao.contar() + 1;
        String codigo;
        do {
            codigo = String.format("PRE-%04d", numero++);
        } while (dao.existeCodigo(codigo));
        return codigo;
    }

    private LocalDate parsearFecha(String texto) {
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
