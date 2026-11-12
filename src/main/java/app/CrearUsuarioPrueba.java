/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import conexion.ConexionMongoDB;
import modelo.Seguridad;
import org.bson.Document;

public class CrearUsuarioPrueba {

    public static void main(String[] args) {
        try {
            MongoDatabase db = ConexionMongoDB.obtenerConexion();
            MongoCollection<Document> coleccionLogin = db.getCollection("login");

            // Crear usuario administrador de prueba si no existe
            Document filtro = new Document("usuario", "admin");
            if (coleccionLogin.find(filtro).first() == null) {
                Document nuevoUsuario = new Document("usuario", "admin")
                        .append("correo", "admin@biblioteca.edu.sv")
                        .append("contrasena", Seguridad.hashear("1234"));

                coleccionLogin.insertOne(nuevoUsuario);
                System.out.println("Usuario de prueba 'admin' creado correctamente (clave: 1234).");
            } else {
                System.out.println("El usuario 'admin' ya existe en la base de datos.");
            }
        } catch (Exception e) {
            System.err.println("Error al crear usuario de prueba: " + e.getMessage());
        }
    }
}