/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import conexion.ConexionMongoDB;
import modelo.Seguridad;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

// Capa de acceso a datos para validar credenciales de login en MongoDB (RF-01)
public class LoginDAO {

    private MongoCollection<Document> getCollection() {
        MongoDatabase db = ConexionMongoDB.obtenerConexion();
        return db.getCollection("login");
    }

    public boolean validarCredenciales(String usuarioOCorreo, String claveTextoPlano) {
        try {
            String claveHash = Seguridad.hashear(claveTextoPlano);
            
            Document query = new Document("$or", java.util.Arrays.asList(
                    new Document("usuario", usuarioOCorreo),
                    new Document("correo", usuarioOCorreo)
            )).append("contrasena", claveHash);

            Document resultado = getCollection().find(query).first();
            return resultado != null;
        } catch (Exception e) {
            System.err.println("Error en LoginDAO: " + e.getMessage());
            return false;
        }
    }
}
