package vista;

import java.time.LocalDate;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
import modelo.Prestamo;

/**
 * Utilidades compartidas por las ventanas de préstamos y devoluciones.
 */
public class UtilVista {

    /** Elemento de una lista desplegable: guarda el código y el texto que se muestra. */
    public static class Opcion {

        public final String codigo;
        public final String texto;

        public Opcion(String codigo, String texto) {
            this.codigo = codigo;
            this.texto = texto;
        }

        @Override
        public String toString() {
            return texto;
        }
    }

    /** Llena una lista desplegable con pares {código, texto}. */
    public static void llenarCombo(JComboBox<Opcion> combo, List<String[]> opciones) {
        combo.removeAllItems();
        for (String[] o : opciones) {
            combo.addItem(new Opcion(o[0], o[1]));
        }
    }

    /** Modelo de tabla (no editable) para mostrar préstamos. */
    public static DefaultTableModel crearModeloPrestamos() {
        String[] columnas = {"Código", "Usuario", "Material", "Fecha préstamo",
            "Fecha prevista", "Fecha devolución", "Estado"};
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    /** Reemplaza el contenido de la tabla por la lista de préstamos. */
    public static void llenarTabla(DefaultTableModel modelo, List<Prestamo> lista) {
        modelo.setRowCount(0);
        LocalDate hoy = LocalDate.now();
        for (Prestamo p : lista) {
            modelo.addRow(new Object[]{
                p.getCodigo(),
                p.getCodigoUsuario(),
                p.getCodigoMaterial(),
                String.valueOf(p.getFechaPrestamo()),
                String.valueOf(p.getFechaPrevista()),
                p.getFechaDevolucion() == null ? "-" : p.getFechaDevolucion().toString(),
                p.getEstadoVisible(hoy)
            });
        }
    }
}
