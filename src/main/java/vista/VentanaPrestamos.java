package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import modelo.Prestamo;
import servicio.PrestamoServicio;
import vista.UtilVista.Opcion;

/**
 * Ventana de gestión de préstamos: registrar un préstamo y consultarlos.
 */
public class VentanaPrestamos extends JFrame {

    private final PrestamoServicio servicio = new PrestamoServicio();

    // Pestaña "Nuevo préstamo"
    private final JComboBox<Opcion> cmbUsuario = new JComboBox<>();
    private final JComboBox<Opcion> cmbMaterial = new JComboBox<>();
    private final JTextField txtFechaPrestamo = new JTextField(12);
    private final JTextField txtFechaPrevista = new JTextField(12);

    // Pestaña "Consultar préstamos"
    private final JTextField txtBuscarCodigo = new JTextField(8);
    private final JTextField txtBuscarUsuario = new JTextField(8);
    private final JTextField txtBuscarMaterial = new JTextField(8);
    private final JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"Todos", "PRESTADO", "DEVUELTO"});
    private final DefaultTableModel modeloTabla = UtilVista.crearModeloPrestamos();
    private final JLabel lblResultado = new JLabel(" ");

    public VentanaPrestamos() {
        setTitle("Gestión de préstamos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 480);
        setLocationRelativeTo(null);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Nuevo préstamo", crearPanelRegistro());
        pestanas.addTab("Consultar préstamos", crearPanelConsulta());
        add(pestanas);

        cargarOpciones();
        consultar(false);
    }

    // Construcción de la interfaz
   
    private JPanel crearPanelRegistro() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 12, 8, 12);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        txtFechaPrestamo.setText(LocalDate.now().toString());
        txtFechaPrevista.setText(LocalDate.now().plusDays(7).toString());

        agregarFila(panel, c, 0, "Usuario:", cmbUsuario);
        agregarFila(panel, c, 1, "Material:", cmbMaterial);
        agregarFila(panel, c, 2, "Fecha del préstamo (AAAA-MM-DD):", txtFechaPrestamo);
        agregarFila(panel, c, 3, "Fecha prevista de devolución (AAAA-MM-DD):", txtFechaPrevista);

        JButton btnRegistrar = new JButton("Registrar préstamo");
        btnRegistrar.addActionListener(e -> registrar());
        JButton btnActualizar = new JButton("Actualizar listas");
        btnActualizar.addActionListener(e -> cargarOpciones());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnRegistrar);
        botones.add(btnActualizar);

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        c.weightx = 0;
        panel.add(botones, c);

        // Espacio sobrante abajo para que el formulario quede arriba
        c.gridy = 5;
        c.weighty = 1;
        panel.add(new JLabel(" "), c);
        return panel;
    }

    private void agregarFila(JPanel panel, GridBagConstraints c, int fila, String texto, JComponent campo) {
        c.gridy = fila;
        c.gridwidth = 1;
        c.gridx = 0;
        c.weightx = 0;
        panel.add(new JLabel(texto), c);
        c.gridx = 1;
        c.weightx = 1;
        panel.add(campo, c);
    }

    private JPanel crearPanelConsulta() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.add(new JLabel("Código:"));
        filtros.add(txtBuscarCodigo);
        filtros.add(new JLabel("Usuario:"));
        filtros.add(txtBuscarUsuario);
        filtros.add(new JLabel("Material:"));
        filtros.add(txtBuscarMaterial);
        filtros.add(new JLabel("Estado:"));
        filtros.add(cmbEstado);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> consultar(true));
        JButton btnTodos = new JButton("Mostrar todos");
        btnTodos.addActionListener(e -> {
            txtBuscarCodigo.setText("");
            txtBuscarUsuario.setText("");
            txtBuscarMaterial.setText("");
            cmbEstado.setSelectedIndex(0);
            consultar(false);
        });
        filtros.add(btnBuscar);
        filtros.add(btnTodos);

        JTable tabla = new JTable(modeloTabla);
        panel.add(filtros, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(lblResultado, BorderLayout.SOUTH);
        return panel;
    }

    

    private void cargarOpciones() {
        try {
            UtilVista.llenarCombo(cmbUsuario, servicio.opcionesUsuarios());
            UtilVista.llenarCombo(cmbMaterial, servicio.opcionesMateriales());
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrar() {
        Opcion usuario = (Opcion) cmbUsuario.getSelectedItem();
        Opcion material = (Opcion) cmbMaterial.getSelectedItem();
        if (usuario == null) {
            JOptionPane.showMessageDialog(this, "No hay usuarios activos para seleccionar. Registre un usuario primero.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (material == null) {
            JOptionPane.showMessageDialog(this, "No hay materiales disponibles para prestar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String resultado = servicio.registrarPrestamo(usuario.codigo, material.codigo,
                txtFechaPrestamo.getText(), txtFechaPrevista.getText());

        if (resultado.startsWith(PrestamoServicio.EXITO)) {
            String codigo = resultado.substring(PrestamoServicio.EXITO.length() + 1);
            JOptionPane.showMessageDialog(this, "Préstamo registrado correctamente.\nCódigo: " + codigo,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            txtFechaPrestamo.setText(LocalDate.now().toString());
            txtFechaPrevista.setText(LocalDate.now().plusDays(7).toString());
            cargarOpciones();
            consultar(false);
        } else {
            JOptionPane.showMessageDialog(this, resultado, "No se pudo registrar el préstamo",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void consultar(boolean avisarSiVacio) {
        try {
            String estado = String.valueOf(cmbEstado.getSelectedItem());
            List<Prestamo> lista = servicio.buscarPrestamos(txtBuscarCodigo.getText(),
                    txtBuscarUsuario.getText(), txtBuscarMaterial.getText(), estado);
            UtilVista.llenarTabla(modeloTabla, lista);
            lblResultado.setText(lista.size() + " préstamo(s) encontrado(s).");
            if (lista.isEmpty() && avisarSiVacio) {
                JOptionPane.showMessageDialog(this, "No se encontraron préstamos con esos criterios.",
                        "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (RuntimeException e) {
            lblResultado.setText("Error al consultar.");
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrestamos().setVisible(true));
    }
}
