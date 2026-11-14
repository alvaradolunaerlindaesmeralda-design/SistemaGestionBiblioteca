package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import modelo.Prestamo;
import servicio.PrestamoServicio;
import vista.UtilVista.Opcion;


public class VentanaDevoluciones extends JFrame {

    private final PrestamoServicio servicio = new PrestamoServicio();
    private final JComboBox<Opcion> cmbPrestamo = new JComboBox<>();
    private final List<Runnable> refrescos = new ArrayList<>();

    public VentanaDevoluciones() {
        setTitle("Devoluciones y reportes de préstamos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 480);
        setLocationRelativeTo(null);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Registrar devolución", crearPanelDevolucion());
        pestanas.addTab("Préstamos activos",
                crearPanelListado(servicio::listarActivos, "No hay préstamos activos."));
        pestanas.addTab("Préstamos vencidos",
                crearPanelListado(servicio::listarVencidos, "No hay préstamos vencidos."));
        pestanas.addTab("Historial",
                crearPanelListado(servicio::listarHistorial, "Todavía no hay préstamos registrados."));
        // Al cambiar de pestaña se vuelven a leer los datos de MongoDB
        pestanas.addChangeListener(e -> refrescarTodo());
        add(pestanas);

        refrescarTodo();
    }

    // ------------------------------------------------------------------
    // Construcción de la interfaz
    // ------------------------------------------------------------------

    private JPanel crearPanelDevolucion() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 20));
        formulario.add(new JLabel("Préstamo activo:"));
        formulario.add(cmbPrestamo);

        JButton btnDevolver = new JButton("Registrar devolución");
        btnDevolver.addActionListener(e -> devolver());
        formulario.add(btnDevolver);

        JButton btnActualizar = new JButton("Actualizar lista");
        btnActualizar.addActionListener(e -> refrescarTodo());
        formulario.add(btnActualizar);

        panel.add(formulario, BorderLayout.NORTH);
        panel.add(new JLabel("  La fecha de devolución que se guarda es la de hoy. "
                + "El material vuelve a estar disponible."), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelListado(Supplier<List<Prestamo>> origen, String mensajeVacio) {
        DefaultTableModel modelo = UtilVista.crearModeloPrestamos();
        JTable tabla = new JTable(modelo);
        JLabel lblEstado = new JLabel(" ");

        Runnable cargar = () -> {
            try {
                List<Prestamo> lista = origen.get();
                UtilVista.llenarTabla(modelo, lista);
                lblEstado.setText(lista.isEmpty() ? mensajeVacio : lista.size() + " registro(s).");
            } catch (RuntimeException e) {
                lblEstado.setText("Error al consultar.");
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        refrescos.add(cargar);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargar.run());
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        arriba.add(btnActualizar);

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(arriba, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(lblEstado, BorderLayout.SOUTH);
        return panel;
    }

    // Acciones//

    private void refrescarTodo() {
        try {
            UtilVista.llenarCombo(cmbPrestamo, servicio.opcionesPrestamosActivos());
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        for (Runnable r : refrescos) {
            r.run();
        }
    }

    private void devolver() {
        Opcion seleccionado = (Opcion) cmbPrestamo.getSelectedItem();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "No hay préstamos activos para devolver.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Registrar la devolución del préstamo " + seleccionado.codigo + "?",
                "Confirmar devolución", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) {
            return;
        }

        String resultado = servicio.registrarDevolucion(seleccionado.codigo);
        if (resultado.startsWith(PrestamoServicio.EXITO)) {
            String detalle = resultado.substring(PrestamoServicio.EXITO.length() + 1);
            JOptionPane.showMessageDialog(this,
                    "Devolución registrada (" + detalle + ").\nEl material ya está disponible.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, resultado, "No se pudo registrar la devolución",
                    JOptionPane.ERROR_MESSAGE);
        }
        refrescarTodo();
    }

    /** Permite abrir esta ventana sola para probarla. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaDevoluciones().setVisible(true));
    }
}
