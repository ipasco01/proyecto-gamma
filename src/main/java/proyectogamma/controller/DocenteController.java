/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.controller;

/**
 *
 * @author Isabel
 */
import proyectogamma.model.Docente;
import proyectogamma.model.BaseDatos;
import proyectogamma.controller.UsuarioController;
import proyectogamma.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocenteController {

    // Método para obtener todos los docentes
    public List<Docente> obtenerDocentes() {
        List<Docente> docentes = new ArrayList<>();
        String sql = "SELECT * FROM docente";

        try (Connection conn = BaseDatos.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Docente docente = new Docente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        rs.getString("especialidad"),
                        rs.getDate("fecha_contratacion"),
                        rs.getTimestamp("fecha_registro")
                );
                docentes.add(docente);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener docentes: " + e.getMessage());
        }

        return docentes;
    }

    public List<Docente> obtenerTodosLosDocentes() {
        String sql = "SELECT * FROM docente";
        List<Docente> docentes = new ArrayList<>();

        try (Connection conn = BaseDatos.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Docente docente = new Docente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        rs.getString("especialidad"),
                        rs.getDate("fecha_contratacion"),
                        rs.getTimestamp("fecha_registro")
                );
                docentes.add(docente);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener docentes: " + e.getMessage());
        }

        return docentes;
    }

    public int obtenerIdProfesorPorNombre(String nombreCompleto) {
        String sql = "SELECT id FROM docente WHERE CONCAT(nombre, ' ', apellido) = ?";
        int idProfesor = -1;

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreCompleto);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                idProfesor = rs.getInt("id");
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener ID del profesor por nombre completo: " + e.getMessage());
        }

        return idProfesor;
    }

    // Método para obtener un docente por ID
    public Docente obtenerDocentePorId(int id) {
        String sql = "SELECT * FROM docente WHERE id = ?";
        Docente docente = null;

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    docente = new Docente(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getString("especialidad"),
                            rs.getDate("fecha_contratacion"),
                            rs.getTimestamp("fecha_registro")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener el docente: " + e.getMessage());
        }

        return docente;
    }

    public boolean agregarDocente(Docente docente, String nombreUsuario, String contrasena) {
        String sqlDocente = "INSERT INTO docente (nombre, apellido, email, especialidad, fecha_contratacion, fecha_registro) "
                + "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        Connection conn = null;
        PreparedStatement pstmtDocente = null;

        try {
            conn = BaseDatos.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // Insertar el docente
            pstmtDocente = conn.prepareStatement(sqlDocente, Statement.RETURN_GENERATED_KEYS);
            pstmtDocente.setString(1, docente.getNombre());
            pstmtDocente.setString(2, docente.getApellido());
            pstmtDocente.setString(3, docente.getEmail());
            pstmtDocente.setString(4, docente.getEspecialidad());
            pstmtDocente.setDate(5, new java.sql.Date(docente.getFechaContratacion().getTime()));

            int rowsInsertedDocente = pstmtDocente.executeUpdate();
            if (rowsInsertedDocente == 0) {
                throw new SQLException("Fallo al insertar el docente.");
            }

            // Obtener el ID del docente recién insertado
            ResultSet generatedKeys = pstmtDocente.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("No se pudo obtener el ID del docente.");
            }
            int idDocente = generatedKeys.getInt(1);

            // Crear el usuario asociado al docente
            UsuarioController usuarioController = new UsuarioController();
            Usuario usuario = new Usuario(0, nombreUsuario, contrasena, "Docente", null, idDocente, null);

            if (!usuarioController.agregarUsuario(usuario)) {
                throw new SQLException("Fallo al insertar el usuario asociado al docente.");
            }

            conn.commit(); // Confirmar la transacción
            return true;

        } catch (SQLException e) {
            System.out.println("Error al agregar docente y usuario: " + e.getMessage());
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
                if (pstmtDocente != null) {
                    pstmtDocente.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException closeEx) {
                System.out.println("Error al cerrar recursos: " + closeEx.getMessage());
            }
        }
    }

    // Método para actualizar un docente existente
    public boolean actualizarDocente(Docente docente) {
        String sql = "UPDATE docente SET nombre = ?, apellido = ?, email = ?, especialidad = ?, fecha_contratacion = ? "
                + "WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, docente.getNombre());
            pstmt.setString(2, docente.getApellido());
            pstmt.setString(3, docente.getEmail());
            pstmt.setString(4, docente.getEspecialidad());
            pstmt.setDate(5, new java.sql.Date(docente.getFechaContratacion().getTime()));
            pstmt.setInt(6, docente.getId());

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar docente: " + e.getMessage());
            return false;
        }
    }

    // Método para eliminar un docente por ID
    public boolean eliminarDocente(int id) {
        String sql = "DELETE FROM docente WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar docente: " + e.getMessage());
            return false;
        }
    }
}
