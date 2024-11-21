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


        try (Connection conn = BaseDatos.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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
        String sql = "INSERT INTO calificacion (id_alumno, id_asignatura, nota, fecha,id_examen) VALUES (?, ?, ?, ?,?)";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, calificacion.getIdAlumno());
            pstmt.setInt(2, calificacion.getIdAsignatura());
            pstmt.setDouble(3, calificacion.getNota());
            pstmt.setTimestamp(4, new Timestamp(calificacion.getFecha().getTime()));
            pstmt.setInt(5, calificacion.getIdExamen());

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