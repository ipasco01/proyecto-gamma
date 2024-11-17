/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Isabel
 */
public class BaseDatos {
  private static Connection connection = null;

    // Configuración de conexión (incluir aquí los datos directamente)
    private static final String URL = "jdbc:mysql://php.ejesxyz.com:3306/proyecto-gamma?connectTimeout=5000";
    private static final String USER = "isa";
    private static final String PASSWORD = "7424@@";

    // Método para obtener una nueva conexión
    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos");
            return connection; // Devuelve una nueva conexión
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
            return null; // Devuelve null si no puede conectar
        }
    }

    // Método para cerrar una conexión específica
    public static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
