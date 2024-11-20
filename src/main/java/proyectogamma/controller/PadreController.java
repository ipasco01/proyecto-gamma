/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.controller;

/**
 *
 * @author Isabel
 */
import proyectogamma.model.Padre;
import proyectogamma.model.BaseDatos;
import proyectogamma.controller.UsuarioController;
import proyectogamma.model.Usuario;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PadreController {

    // Método para obtener todos los padres
    public List<Padre> obtenerPadres() {
        List<Padre> padres = new ArrayList<>();
        String sql = "SELECT * FROM padre";

        try (Connection conn = BaseDatos.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Padre padre = new Padre(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        rs.getString("telefono"),
                        rs.getString("direccion"),
                        rs.getTimestamp("fecha_registro")
                );
                padres.add(padre);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener padres: " + e.getMessage());
        }

        return padres;
    }

    // Método para obtener un padre por ID
    public Padre obtenerPadrePorId(int id) {
        String sql = "SELECT * FROM padre WHERE id = ?";
        Padre padre = null;

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    padre = new Padre(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getString("telefono"),
                            rs.getString("direccion"),
                            rs.getTimestamp("fecha_registro")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener el padre: " + e.getMessage());
        }

        return padre;
    }

    
public boolean agregarPadre(Padre padre, String nombreUsuario, String contrasena) {
    String sqlPadre = "INSERT INTO padre (nombre, apellido, email, telefono, direccion, fecha_registro) " +
                      "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
    Connection conn = null;
    PreparedStatement pstmtPadre = null;

    try {
        conn = BaseDatos.getConnection();
        conn.setAutoCommit(false); // Iniciar transacción

        // Insertar el padre
        pstmtPadre = conn.prepareStatement(sqlPadre, Statement.RETURN_GENERATED_KEYS);
        pstmtPadre.setString(1, padre.getNombre());
        pstmtPadre.setString(2, padre.getApellido());
        pstmtPadre.setString(3, padre.getEmail());
        pstmtPadre.setString(4, padre.getTelefono());
        pstmtPadre.setString(5, padre.getDireccion());

        int rowsInsertedPadre = pstmtPadre.executeUpdate();
        if (rowsInsertedPadre == 0) {
            throw new SQLException("Fallo al insertar el padre.");
        }

        // Obtener el ID del padre recién insertado
        ResultSet generatedKeys = pstmtPadre.getGeneratedKeys();
        if (!generatedKeys.next()) {
            throw new SQLException("No se pudo obtener el ID del padre.");
        }
        int idPadre = generatedKeys.getInt(1);

        // Crear el usuario asociado al padre
        UsuarioController usuarioController = new UsuarioController();
        Usuario usuario = new Usuario(0, nombreUsuario, contrasena, "Padre", null, null, idPadre);

        if (!usuarioController.agregarUsuario(usuario)) {
            throw new SQLException("Fallo al insertar el usuario asociado al padre.");
        }

        conn.commit(); // Confirmar la transacción
        return true;

    } catch (SQLException e) {
        System.out.println("Error al agregar padre y usuario: " + e.getMessage());
        if (conn != null) {
            try {
                conn.rollback(); // Revertir la transacción en caso de error
            } catch (SQLException rollbackEx) {
                System.out.println("Error al hacer rollback: " + rollbackEx.getMessage());
            }
        }
        return false;

    } finally {
        try {
            if (pstmtPadre != null) pstmtPadre.close();
            if (conn != null) conn.close();
        } catch (SQLException closeEx) {
            System.out.println("Error al cerrar recursos: " + closeEx.getMessage());
        }
    }
}

    // Método para actualizar un padre existente
    public boolean actualizarPadre(Padre padre) {
        String sql = "UPDATE padre SET nombre = ?, apellido = ?, email = ?, telefono = ?, direccion = ? " +
                     "WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, padre.getNombre());
            pstmt.setString(2, padre.getApellido());
            pstmt.setString(3, padre.getEmail());
            pstmt.setString(4, padre.getTelefono());
            pstmt.setString(5, padre.getDireccion());
            pstmt.setInt(6, padre.getId());

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar el padre: " + e.getMessage());
            return false;
        }
    }

    // Método para eliminar un padre por ID
    public boolean eliminarPadre(int id) {
        String sql = "DELETE FROM padre WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar el padre: " + e.getMessage());
            return false;
        }
    }
}