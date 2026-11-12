/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import conexion.ConexionMongoDB;
import modelo.Usuario;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

// Capa de acceso a datos exclusiva para la colección "usuarios" de MongoDB (RF-03)
public class UsuarioDAO {

    private MongoCollection<Document> getCollection() {
        MongoDatabase db = ConexionMongoDB.obtenerConexion();
        return db.getCollection("usuarios");
    }

    public boolean guardar(Usuario usuario) {
        try {
            getCollection().insertOne(usuario.toDocument());
            return true;
        } catch (Exception e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
            return false;
        }
    }

    public Usuario buscarPorCodigo(String codigo) {
        Document doc = getCollection().find(new Document("codigo", codigo)).first();
        return Usuario.fromDocument(doc);
    }

    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = new ArrayList<>();
        for (Document doc : getCollection().find()) {
            lista.add(Usuario.fromDocument(doc));
        }
        return lista;
    }

    public boolean actualizar(Usuario usuario) {
        try {
            getCollection().replaceOne(
                    new Document("codigo", usuario.getCodigo()),
                    usuario.toDocument()
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String codigo) {
        try {
            getCollection().deleteOne(new Document("codigo", codigo));
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
}
