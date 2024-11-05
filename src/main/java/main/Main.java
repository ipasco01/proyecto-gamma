/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;
import main.model.BaseDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Llama al método de prueba de conexión
        probarConexion();
    }

    public static void probarConexion() {
        // Intenta conectar y ejecutar una consulta simple
        try (Connection conn = BaseDatos.getConnection()) {
            if (conn != null) {
                System.out.println("Conexión exitosa. Probando consulta...");

                // Realiza una consulta simple para verificar la conexión
                String sql = "SELECT 1";
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Consulta de prueba exitosa: " + rs.getInt(1));
                    }
                }
            } else {
                System.out.println("No se pudo establecer la conexión.");
            }
        } catch (SQLException e) {
            System.out.println("Error en la prueba de conexión: " + e.getMessage());
        } finally {
            BaseDatos.closeConnection();
        }
    }
}
