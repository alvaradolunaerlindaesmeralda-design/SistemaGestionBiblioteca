package modelo;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import org.bson.Document;

/**
 * Representa el préstamo de un material (libro o revista) a un usuario.
 * Se usa un préstamo por material. Las fechas se guardan en MongoDB como
 * texto ISO (AAAA-MM-DD) para evitar problemas de zona horaria.
 */
public class Prestamo {

    public static final String PRESTADO = "PRESTADO";
    public static final String DEVUELTO = "DEVUELTO";

    private String codigo;
    private String codigoUsuario;
    private String codigoMaterial;
    private LocalDate fechaPrestamo;
    private LocalDate fechaPrevista;
    private LocalDate fechaDevolucion;
    private String estado;

    public Prestamo() {
        this.estado = PRESTADO;
    }

    public Prestamo(String codigo, String codigoUsuario, String codigoMaterial,
            LocalDate fechaPrestamo, LocalDate fechaPrevista) {
        this.codigo = codigo;
        this.codigoUsuario = codigoUsuario;
        this.codigoMaterial = codigoMaterial;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaPrevista = fechaPrevista;
        this.fechaDevolucion = null;
        this.estado = PRESTADO;
    }

    /** Convierte el préstamo en un documento para guardarlo en MongoDB. */
    public Document toDocument() {
        return new Document("codigo", codigo)
                .append("codigoUsuario", codigoUsuario)
                .append("codigoMaterial", codigoMaterial)
                .append("fechaPrestamo", fechaPrestamo.toString())
                .append("fechaPrevista", fechaPrevista.toString())
                .append("fechaDevolucion", fechaDevolucion == null ? null : fechaDevolucion.toString())
                .append("estado", estado);
    }

    /** Reconstruye un préstamo a partir de un documento de MongoDB. */
    public static Prestamo fromDocument(Document doc) {
        if (doc == null) {
            return null;
        }
        Prestamo p = new Prestamo();
        p.codigo = doc.getString("codigo");
        p.codigoUsuario = doc.getString("codigoUsuario");
        p.codigoMaterial = doc.getString("codigoMaterial");
        p.fechaPrestamo = leerFecha(doc.getString("fechaPrestamo"));
        p.fechaPrevista = leerFecha(doc.getString("fechaPrevista"));
        p.fechaDevolucion = leerFecha(doc.getString("fechaDevolucion"));
        String est = doc.getString("estado");
        p.estado = (est == null) ? PRESTADO : est;
        return p;
    }

    private static LocalDate leerFecha(String texto) {
        if (texto == null || texto.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** Un préstamo está vencido si sigue activo y su fecha prevista ya pasó. */
    public boolean estaVencido(LocalDate hoy) {
        return PRESTADO.equals(estado) && fechaPrevista != null && fechaPrevista.isBefore(hoy);
    }

    /** Días de retraso respecto a la fecha dada (0 si no hay retraso). */
    public int diasRetraso(LocalDate referencia) {
        if (fechaPrevista == null) {
            return 0;
        }
        long dias = ChronoUnit.DAYS.between(fechaPrevista, referencia);
        return dias > 0 ? (int) dias : 0;
    }

    public String getEstadoVisible(LocalDate hoy) {
        return estaVencido(hoy) ? estado + " - VENCIDO" : estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCodigoUsuario() {
        return codigoUsuario;
    }

    public String getCodigoMaterial() {
        return codigoMaterial;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDate getFechaPrevista() {
        return fechaPrevista;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public String getEstado() {
        return estado;
    }
}
