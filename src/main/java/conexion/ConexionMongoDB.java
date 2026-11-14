package conexion;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.bson.Document;


 
public class ConexionMongoDB {

    private static final String URI_POR_DEFECTO = "mongodb://localhost:27017";
    private static final String BASE_POR_DEFECTO = "BibliotecaDB";

    private static MongoClient cliente;
    private static String nombreBase = BASE_POR_DEFECTO;

    public static synchronized MongoDatabase obtenerConexion() {
        if (cliente == null) {
            Properties config = cargarConfiguracion();
            nombreBase = config.getProperty("mongo.base", BASE_POR_DEFECTO);
            cliente = MongoClients.create(config.getProperty("mongo.uri", URI_POR_DEFECTO));
        }
        return cliente.getDatabase(nombreBase);
    }

    public static String probarConexion() {
        try {
            obtenerConexion().runCommand(new Document("ping", 1));
            return null;
        } catch (Exception e) {
            cerrarConexion();
            return "No se pudo conectar con MongoDB. Verifique que el servicio esté iniciado y la "
                    + "configuración en config.properties. Detalle: " + e.getMessage();
        }
    }

    public static synchronized void cerrarConexion() {
        if (cliente != null) {
            cliente.close();
            cliente = null;
        }
    }

    private static Properties cargarConfiguracion() {
        Properties config = new Properties();
        try (FileInputStream in = new FileInputStream("config.properties")) {
            config.load(in);
        } catch (IOException e) {
            
        }
        return config;
    }
}