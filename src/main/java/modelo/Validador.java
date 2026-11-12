/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.regex.Pattern;

// Clase utilitaria con las reglas de validación comunes para todo el equipo (RF-04)
public class Validador {

    private static final Pattern CORREO_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public static boolean esVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    public static boolean esCorreoValido(String correo) {
        if (esVacio(correo)) return false;
        return CORREO_PATTERN.matcher(correo).matches();
    }

    public static boolean esEnteroNoNegativo(String texto) {
        try {
            int val = Integer.parseInt(texto.trim());
            return val >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean esAnioRazonable(String texto) {
        try {
            int anio = Integer.parseInt(texto.trim());
            return anio >= 1500 && anio <= 2026;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
