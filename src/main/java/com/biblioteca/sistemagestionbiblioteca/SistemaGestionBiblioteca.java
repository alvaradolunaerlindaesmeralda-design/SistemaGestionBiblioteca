  package com.biblioteca.sistemagestionbiblioteca;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class SistemaGestionBiblioteca {

    public static void main(String[] args) {

        try {
            RegistroMateriales registro = new RegistroMateriales();

            Revista revista = new Revista(
                    "R001",
                    "National Geographic",
                    2025,
                    125,
                    "Mensual"
            );

            registro.registrarRevista(revista);

            System.out.println("Revista registrada correctamente.");

            MongoDatabase baseDatos = ConexionMongoDB.obtenerConexion();

            MongoCollection<Document> coleccion =
                    baseDatos.getCollection("Materiales");

            System.out.println("\nRegistro guardado:");

            for (Document documento : coleccion.find(
                    new Document("codigo", "R001"))) {
                System.out.println(documento.toJson());
            }

            ConexionMongoDB.cerrarConexion();

        } catch (Exception e) {
            System.out.println("Error al registrar la revista.");
            System.out.println(e.getMessage());
        }
    }
}