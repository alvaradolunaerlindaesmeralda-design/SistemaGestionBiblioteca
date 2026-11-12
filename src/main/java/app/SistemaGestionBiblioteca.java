package app;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import vista.Login;
// Si deseas probar directo sin pasar por el Login, descomenta la siguiente línea:
// import vista.VentanaPrincipal;

public class SistemaGestionBiblioteca {

    public static void main(String[] args) {
        // Aplica el estilo visual nativo del sistema operativo (Windows)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("No se pudo cargar el estilo visual: " + e.getMessage());
        }

        // Inicia la interfaz gráfica del sistema
        SwingUtilities.invokeLater(() -> {
            Login ventanaLogin = new Login();
            ventanaLogin.setLocationRelativeTo(null); // Centra la ventana en pantalla
            ventanaLogin.setVisible(true);
            
            // Si prefieres abrir directo el menú principal para probar tu módulo:
            // new VentanaPrincipal().setVisible(true);
        });
    }
}