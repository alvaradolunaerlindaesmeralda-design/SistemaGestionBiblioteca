/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;

import conexion.ConexionMongoDB;
import modelo.Seguridad;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

// Clase utilitaria para inicializar el usuario de prueba en MongoDB (RF-01)
public class CrearUsuarioPrueba {

    public static void main(String[] args) {
        try {
            MongoDatabase db = ConexionMongoDB.obtenerConexion();
            MongoCollection<Document> coleccionLogin = db.getCollection("login");

            // Datos del usuario de prueba
            String usuarioPrueba = "admin";
            String correoPrueba = "admin@biblioteca.com";
            String claveEnTextoPlano = "admin123";

            // Encriptamos la contraseña con SHA-256 (no se guarda texto plano)
            String claveHash = Seguridad.hashear(claveEnTextoPlano);

            // Verificar si el usuario ya existe en MongoDB
            Document existe = coleccionLogin.find(new Document("usuario", usuarioPrueba)).first();

            if (existe == null) {
                Document docUsuario = new Document("usuario", usuarioPrueba)
                        .append("correo", correoPrueba)
                        .append("contrasena", claveHash);

                coleccionLogin.insertOne(docUsuario);
                System.out.println("--------------------------------------------------";);
                System.out.println("¡Usuario de prueba creado con éxito!");
                System.out.println("Usuario: " + usuarioPrueba);
                System.out.println("Correo: " + correoPrueba);
                System.out.println("Contraseña en texto plano: " + claveEnTextoPlano);
                System.out.println("Hash guardado en MongoDB: " + claveHash);
                System.out.println("--------------------------------------------------";);
            } else {
                System.out.println("El usuario de prueba '" + usuarioPrueba + "' ya existe en la base de datos.");
            }

        } catch (Exception e) {
            System.err.println("Error al crear el usuario de prueba: " + e.getMessage());
        } finally {
            ConexionMongoDB.cerrarConexion();
        }
    }
}
