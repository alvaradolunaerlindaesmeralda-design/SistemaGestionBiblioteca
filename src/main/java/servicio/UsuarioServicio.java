/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import dao.UsuarioDAO;
import conexion.ConexionMongoDB;
import modelo.Usuario;
import modelo.Validador;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.List;

// Capa de lógica de negocio para validaciones y reglas de usuarios (RF-02, RF-04)
public class UsuarioServicio {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public String registrarUsuario(Usuario u) {
        if (Validador.esVacio(u.getCodigo()) || Validador.esVacio(u.getNombreCompleto())
                || Validador.esVacio(u.getDocumento()) || Validador.esVacio(u.getContacto())) {
            return "Todos los campos son obligatorios.";
        }
        if (usuarioDAO.buscarPorCodigo(u.getCodigo()) != null) {
            return "El código de usuario ya se encuentra registrado.";
        }
        boolean ok = usuarioDAO.guardar(u);
        return ok ? "EXITO" : "Error de conexión al registrar en MongoDB.";
    }

    public String modificarUsuario(Usuario u) {
        if (Validador.esVacio(u.getNombreCompleto()) || Validador.esVacio(u.getDocumento())
                || Validador.esVacio(u.getContacto())) {
            return "Todos los campos son obligatorios.";
        }
        boolean ok = usuarioDAO.actualizar(u);
        return ok ? "EXITO" : "Error al actualizar el usuario en MongoDB.";
    }

    public String eliminarOInactivar(String codigo, boolean soloInactivar) {
        if (tienePrestamosActivos(codigo)) {
            return "No se puede eliminar ni inactivar: el usuario tiene préstamos activos.";
        }
        
        Usuario u = usuarioDAO.buscarPorCodigo(codigo);
        if (u == null) return "Usuario no encontrado.";

        if (soloInactivar) {
            u.setActivo(false);
            boolean ok = usuarioDAO.actualizar(u);
            return ok ? "EXITO_INACTIVAR" : "Error al inactivar usuario.";
        } else {
            boolean ok = usuarioDAO.eliminar(codigo);
            return ok ? "EXITO_ELIMINAR" : "Error al eliminar usuario.";
        }
    }

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.obtenerTodos();
    }

    public Usuario buscarPorCodigo(String codigo) {
        return usuarioDAO.buscarPorCodigo(codigo);
    }

    // Regla de negocio: verifica si el usuario tiene préstamos pendientes en MongoDB
    private boolean tienePrestamosActivos(String codigoUsuario) {
        try {
            MongoDatabase db = ConexionMongoDB.obtenerConexion();
            Document query = new Document("codigoUsuario", codigoUsuario)
                    .append("estado", "PRESTADO");
            return db.getCollection("prestamos").find(query).first() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
