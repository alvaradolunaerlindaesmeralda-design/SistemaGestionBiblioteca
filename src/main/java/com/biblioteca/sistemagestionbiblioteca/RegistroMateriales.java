 
package com.biblioteca.sistemagestionbiblioteca;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class RegistroMateriales {

    private final MongoCollection<Document> coleccion;

    public RegistroMateriales() {
        MongoDatabase baseDatos = ConexionMongoDB.obtenerConexion();
        coleccion = baseDatos.getCollection("Materiales");
    }

    public void registrarLibro(Libro libro) {

        Document documento = new Document("codigo", libro.getCodigo())
                .append("titulo", libro.getTitulo())
                .append("anioPublicacion", libro.getAnioPublicacion())
                .append("autor", libro.getAutor())
                .append("editorial", libro.getEditorial());

        coleccion.insertOne(documento);
    }
    public void registrarRevista(Revista revista) {

    Document documento = new Document("codigo", revista.getCodigo())
            .append("titulo", revista.getTitulo())
            .append("anioPublicacion", revista.getAnioPublicacion())
            .append("numeroEdicion", revista.getNumeroEdicion())
            .append("periodicidad", revista.getPeriodicidad());

    coleccion.insertOne(documento);
}
}
