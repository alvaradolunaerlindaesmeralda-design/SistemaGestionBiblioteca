package conexion;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class ConexionMongoDB {

    private static final String URI = "mongodb://localhost:27017";

    private static MongoClient cliente;

    public static MongoDatabase obtenerConexion() {

        if (cliente == null) {
            cliente = MongoClients.create(URI);
        }

        return cliente.getDatabase("BibliotecaDB");
    }

    public static void cerrarConexion() {
        if (cliente != null) {
            cliente.close();
            cliente = null;
        }
    }
}