package mx.uv.fei.infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/registro_visita_fei";
    private static final String USER = "fei_visits_svc";
    private static final String PASSWORD = "Fei_2026!";
    
    private static Connection connection;

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error: No se encontró el driver de MySQL.", e);
        } catch (SQLException e) {
            throw new SQLException("Error al conectar a la base de datos: ", e);
        }
        return connection;
    }
    
    public static void closeConnection() throws SQLException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            throw new SQLException("Error al cerrar la conexión: ", e);
        }
    }
}