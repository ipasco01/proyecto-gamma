/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.controller;

/**
 *
 * @author Isabel
 */

import proyectogamma.model.Calificacion;
import proyectogamma.model.BaseDatos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CalificacionController {

    // Método para obtener todas las calificaciones
    public List<Calificacion> obtenerCalificaciones() {
        List<Calificacion> calificaciones = new ArrayList<>();
        String sql = "SELECT * FROM calificacion";

        try (Connection conn = BaseDatos.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Calificacion calificacion = new Calificacion(
                        rs.getInt("id"),
                        rs.getInt("id_alumno"),
                        rs.getInt("id_asignatura"),
                        rs.getDouble("nota"),
                        rs.getTimestamp("fecha")
                );
                calificaciones.add(calificacion);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener calificaciones: " + e.getMessage());
        }

        return calificaciones;
    }

    // Método para obtener una calificación por ID
    public Calificacion obtenerCalificacionPorId(int id) {
        String sql = "SELECT * FROM calificacion WHERE id = ?";
        Calificacion calificacion = null;

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    calificacion = new Calificacion(
                            rs.getInt("id"),
                            rs.getInt("id_alumno"),
                            rs.getInt("id_asignatura"),
                            rs.getDouble("nota"),
                            rs.getTimestamp("fecha")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener la calificación: " + e.getMessage());
        }

        return calificacion;
    }

    // Método para agregar una nueva calificación
    public boolean agregarCalificacion(Calificacion calificacion) {
        String sql = "INSERT INTO calificacion (id_alumno, id_asignatura, nota, fecha) VALUES (?, ?, ?, ?)";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, calificacion.getIdAlumno());
            pstmt.setInt(2, calificacion.getIdAsignatura());
            pstmt.setDouble(3, calificacion.getNota());
            pstmt.setTimestamp(4, new Timestamp(calificacion.getFecha().getTime()));

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println("Error al agregar calificación: " + e.getMessage());
            return false;
        }
    }

    // Método para actualizar una calificación existente
    public boolean actualizarCalificacion(Calificacion calificacion) {
        String sql = "UPDATE calificacion SET id_alumno = ?, id_asignatura = ?, nota = ?, fecha = ? WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, calificacion.getIdAlumno());
            pstmt.setInt(2, calificacion.getIdAsignatura());
            pstmt.setDouble(3, calificacion.getNota());
            pstmt.setTimestamp(4, new Timestamp(calificacion.getFecha().getTime()));
            pstmt.setInt(5, calificacion.getId());

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar calificación: " + e.getMessage());
            return false;
        }
    }

    // Método para eliminar una calificación por ID
    public boolean eliminarCalificacion(int id) {
        String sql = "DELETE FROM calificacion WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar calificación: " + e.getMessage());
            return false;
        }
    }
}