package conector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import excepciones.ConexionFallidaException;

public class DatabaseConnector {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/tiempoextrabd";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "";

    public static Connection getConnection() throws ConexionFallidaException {
        try {
            Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
            // Aquí simplemente retornamos la conexión. No lanzamos una excepción.
            return connection;
        } catch (SQLException ex) {
            throw new ConexionFallidaException("Fallo al conectar a la base de datos.");
        }
    }
}

