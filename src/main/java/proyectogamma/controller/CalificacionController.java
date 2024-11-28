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
        String sql = """
    SELECT c.*, a.nombre AS nombre_asignatura, e.nombre AS nombre_examen
    FROM calificacion c
    JOIN asignatura a ON c.id_asignatura = a.id
    JOIN evaluaciones e ON c.id_examen = e.id
""";

        try (Connection conn = BaseDatos.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Calificacion calificacion = new Calificacion(
                        rs.getInt("id"),
                        rs.getInt("id_alumno"),
                        rs.getInt("id_asignatura"),
                        rs.getDouble("nota"),
                        rs.getInt("id_examen"),
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

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {

                    calificacion = new Calificacion(
                            rs.getInt("id"),
                            rs.getInt("id_alumno"),
                            rs.getInt("id_asignatura"),
                            rs.getDouble("nota"),
                            rs.getInt("id_examen"),
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
        String sql = "INSERT INTO calificacion (id_alumno, id_asignatura, nota, id_examen, fecha) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, calificacion.getIdAlumno());
            pstmt.setInt(2, calificacion.getIdAsignatura());
            pstmt.setDouble(3, calificacion.getNota());
            pstmt.setInt(4, calificacion.getIdExamen());
            pstmt.setTimestamp(5, new Timestamp(calificacion.getFecha().getTime()));

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println("Error al agregar calificación: " + e.getMessage());
            return false;
        }
    }

    // Método para eliminar una calificación por ID
    public boolean eliminarCalificacion(int id) {
        String sql = "DELETE FROM calificacion WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar calificación: " + e.getMessage());
            return false;
        }
    }

    public List<Calificacion> obtenerCalificacionesPorAlumnoYAsignatura(int idAlumno, int idAsignatura) {
        List<Calificacion> calificaciones = new ArrayList<>();
        String sql = "SELECT c.id, e.nombre AS titulo, c.nota, e.peso, c.fecha "
                + "FROM calificacion c "
                + "JOIN evaluaciones e ON c.id_examen = e.id "
                + "WHERE c.id_alumno = ? AND c.id_asignatura = ?";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idAlumno);
            pstmt.setInt(2, idAsignatura);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Calificacion calificacion = new Calificacion(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getDouble("nota"),
                        rs.getDouble("peso"),
                        rs.getDate("fecha")
                );
                calificaciones.add(calificacion);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener calificaciones: " + e.getMessage());
        }
        return calificaciones;
    }

    public List<Calificacion> obtenerCalificacionesPorEvaluacion(int idEvaluacion) {
        List<Calificacion> calificaciones = new ArrayList<>();
        String sql = "SELECT * FROM calificacion WHERE id_examen = ?";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEvaluacion);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Calificacion calificacion = new Calificacion(
                            rs.getInt("id"),
                            rs.getInt("id_alumno"),
                            rs.getInt("id_asignatura"),
                            rs.getDouble("nota"),
                            rs.getInt("id_examen"),
                            rs.getTimestamp("fecha")
                    );
                    calificaciones.add(calificacion);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener calificaciones por evaluación: " + e.getMessage());
        }

        return calificaciones;
    }

    public boolean actualizarCalificacion(Calificacion calificacion) {
        String sql = "UPDATE calificacion SET nota = ?, fecha = ?, id_examen = ? WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Asignar los valores a la consulta preparada
            pstmt.setDouble(1, calificacion.getNota());
            pstmt.setTimestamp(2, new Timestamp(calificacion.getFecha().getTime()));
            pstmt.setInt(3, calificacion.getIdExamen());
            pstmt.setInt(4, calificacion.getId()); // ID de la calificación que se actualizará

            // Ejecutar la actualización
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0; // Retorna true si se actualizó al menos una fila

        } catch (SQLException e) {
            System.out.println("Error al actualizar calificación: " + e.getMessage());
            return false;
        }
    }

}
