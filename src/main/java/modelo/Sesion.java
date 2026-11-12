/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

// Clase estática para controlar la sesión activa del usuario autenticado (RF-06)
public class Sesion {

    private static String usuarioActual = null;
    private static String correoActual = null;

    public static void iniciarSesion(String usuario, String correo) {
        usuarioActual = usuario;
        correoActual = correo;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
        correoActual = null;
    }

    public static boolean haySesionActiva() {
        return usuarioActual != null;
    }

    public static String getUsuarioActual() { return usuarioActual; }
    public static String getCorreoActual() { return correoActual; }
}
