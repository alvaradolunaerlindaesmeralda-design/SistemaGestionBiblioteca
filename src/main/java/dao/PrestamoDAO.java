package dao;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import conexion.ConexionMongoDB;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import modelo.Prestamo;
import org.bson.Document;
import org.bson.conversions.Bson;

public class PrestamoDAO {

    private MongoCollection<Document> prestamos() {
        return ConexionMongoDB.obtenerConexion().getCollection("prestamos");
    }

    private MongoCollection<Document> materiales() {
        return ConexionMongoDB.obtenerConexion().getCollection("Materiales");
    }

    private MongoCollection<Document> usuarios() {
        return ConexionMongoDB.obtenerConexion().getCollection("usuarios");
    }

    // ---------- Préstamos ----------

    public void guardar(Prestamo prestamo) {
        prestamos().insertOne(prestamo.toDocument());
    }

    public Prestamo buscarPorCodigo(String codigo) {
        return Prestamo.fromDocument(prestamos().find(Filters.eq("codigo", codigo)).first());
    }

    public boolean existeCodigo(String codigo) {
        return prestamos().find(Filters.eq("codigo", codigo)).first() != null;
    }

    public long contar() {
        return prestamos().countDocuments();
    }

    public List<Prestamo> listar(Bson filtro) {
        List<Prestamo> lista = new ArrayList<>();
        FindIterable<Document> resultado = (filtro == null) ? prestamos().find() : prestamos().find(filtro);
        for (Document doc : resultado.sort(Sorts.ascending("codigo"))) {
            lista.add(Prestamo.fromDocument(doc));
        }
        return lista;
    }

  
    public boolean marcarDevuelto(String codigo, LocalDate fecha) {
        UpdateResult r = prestamos().updateOne(
                Filters.and(Filters.eq("codigo", codigo), Filters.eq("estado", Prestamo.PRESTADO)),
                Updates.combine(
                        Updates.set("estado", Prestamo.DEVUELTO),
                        Updates.set("fechaDevolucion", fecha.toString())));
        return r.getModifiedCount() > 0;
    }

    public void reabrir(String codigo) {
        prestamos().updateOne(
                Filters.eq("codigo", codigo),
                Updates.combine(
                        Updates.set("estado", Prestamo.PRESTADO),
                        Updates.set("fechaDevolucion", null)));
    }

    public void eliminar(String codigo) {
        prestamos().deleteOne(Filters.eq("codigo", codigo));
    }

    // ---------- Materiales ----------

    public Document buscarMaterial(String codigo) {
        return materiales().find(Filters.eq("codigo", codigo)).first();
    }

    public List<Document> listarMateriales() {
        List<Document> lista = new ArrayList<>();
        for (Document doc : materiales().find().sort(Sorts.ascending("codigo"))) {
            lista.add(doc);
        }
        return lista;
    }

    /**
     * Ejemplares disponibles de un material. Si el documento todavía no tiene
     * el campo "cantidadDisponible" se asume 1 ejemplar (valor provisional
     * hasta que el módulo de materiales guarde la cantidad).
     */
    public static int cantidadDisponible(Document material) {
        Object valor = material.get("cantidadDisponible");
        return (valor instanceof Number) ? ((Number) valor).intValue() : 1;
    }

    /**
     * Descuenta un ejemplar de forma atómica. Devuelve false si no quedan ejemplares.
     */
    public boolean tomarEjemplar(String codigoMaterial) {
        UpdateResult r = materiales().updateOne(
                Filters.and(Filters.eq("codigo", codigoMaterial), Filters.gt("cantidadDisponible", 0)),
                Updates.inc("cantidadDisponible", -1));
        if (r.getModifiedCount() > 0) {
            return true;
        }
        // Material sin el campo: se asume 1 ejemplar y se deja el campo en 0.
        r = materiales().updateOne(
                Filters.and(Filters.eq("codigo", codigoMaterial), Filters.exists("cantidadDisponible", false)),
                Updates.set("cantidadDisponible", 0));
        return r.getModifiedCount() > 0;
    }

    /** Suma un ejemplar al material (devolución o deshacer un préstamo fallido). */
    public void devolverEjemplar(String codigoMaterial) {
        materiales().updateOne(
                Filters.eq("codigo", codigoMaterial),
                Updates.inc("cantidadDisponible", 1));
    }

    // ---------- Usuarios ----------

    public Document buscarUsuario(String codigo) {
        return usuarios().find(Filters.eq("codigo", codigo)).first();
    }

    public List<Document> listarUsuariosActivos() {
        List<Document> lista = new ArrayList<>();
        for (Document doc : usuarios().find(Filters.ne("activo", false)).sort(Sorts.ascending("codigo"))) {
            lista.add(doc);
        }
        return lista;
    }
}
