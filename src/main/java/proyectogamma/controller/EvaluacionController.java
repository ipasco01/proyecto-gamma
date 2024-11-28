/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import proyectogamma.model.BaseDatos;
import proyectogamma.model.Calificacion;
import proyectogamma.model.Evaluacion;

/**
 *
 * @author GHV 23
 */
public class EvaluacionController {
    // Método para actualizar una calificación existente

    public boolean actualizarCalificacion(Calificacion calificacion) {
        String sql = "UPDATE calificacion SET id_alumno = ?, id_asignatura = ?, nota = ?, fecha = ?, id_examen=? WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, calificacion.getIdAlumno());
            pstmt.setInt(2, calificacion.getIdAsignatura());
            pstmt.setDouble(3, calificacion.getNota());
            pstmt.setTimestamp(4, new Timestamp(calificacion.getFecha().getTime()));
            pstmt.setInt(5, calificacion.getId());
            pstmt.setInt(5, calificacion.getIdExamen());

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar calificación: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEvaluacion(Evaluacion evaluacion) {
        String sql = "UPDATE evaluaciones SET nombre = ?, peso = ?, id_asignatura = ?, fecha = ? WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, evaluacion.getNombre()); // Nombre de la evaluación
            pstmt.setDouble(2, evaluacion.getPeso()); // Peso
            pstmt.setInt(3, evaluacion.getIdAsignatura()); // ID de la asignatura
            pstmt.setDate(4, new java.sql.Date(evaluacion.getFecha().getTime())); // Fecha
            pstmt.setInt(5, evaluacion.getId()); // ID de la evaluación a actualizar

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar evaluación: " + e.getMessage());
            return false;
        }
    }
    public int getIdEvaluacion(String nombre, int idAsignatura) {
    String sql = "SELECT id FROM evaluaciones WHERE nombre = ? AND id_asignatura = ?";
    int idEvaluacion = -1;

    try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, nombre);
        pstmt.setInt(2, idAsignatura);

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                idEvaluacion = rs.getInt("id");
            }
        }
    } catch (SQLException e) {
        System.out.println("Error al obtener ID de evaluación: " + e.getMessage());
    }

    return idEvaluacion;
}


    public boolean agregarEvaluacion(Evaluacion evaluacion) {
        String sql = "INSERT INTO evaluaciones (nombre, peso, id_asignatura, fecha) VALUES (?, ?, ?, ?)";
        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, evaluacion.getNombre());
            pstmt.setDouble(2, evaluacion.getPeso());
            pstmt.setInt(3, evaluacion.getIdAsignatura());
            pstmt.setDate(4, new java.sql.Date(evaluacion.getFecha().getTime()));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al agregar evaluación: " + e.getMessage());
            return false;
        }
    }

    public List<Evaluacion> obtenerEvaluacionesPorAsignatura(int idAsignatura) {
        List<Evaluacion> evaluaciones = new ArrayList<>();
        String sql = "SELECT * FROM evaluaciones WHERE id_asignatura = ?";
        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idAsignatura);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Evaluacion evaluacion = new Evaluacion(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("peso"),
                        rs.getInt("id_asignatura"),
                        rs.getDate("fecha")
                );
                evaluaciones.add(evaluacion);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener evaluaciones: " + e.getMessage());
        }
        return evaluaciones;
    }

    public boolean eliminarEvaluacion(int idEvaluacion) {
        String sql = "DELETE FROM evaluaciones WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idEvaluacion);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar la evaluación: " + e.getMessage());
            return false;
        }
    }

    public List<Evaluacion> obtenerEvaluacionesFaltantesPorAlumno(int idAlumno, int idAsignatura) {
        List<Evaluacion> evaluaciones = new ArrayList<>();
        String sql = """
        SELECT e.id, e.nombre, e.peso, e.id_asignatura, e.fecha
        FROM evaluaciones e
        LEFT JOIN calificacion c ON e.id = c.id_examen AND c.id_alumno = ?
        WHERE e.id_asignatura = ? AND c.id IS NULL
    """;

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAlumno);
            pstmt.setInt(2, idAsignatura);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Evaluacion evaluacion = new Evaluacion(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getDouble("peso"),
                            rs.getInt("id_asignatura"),
                            rs.getDate("fecha")
                    );
                    evaluaciones.add(evaluacion);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener evaluaciones faltantes: " + e.getMessage());
        }

        return evaluaciones;
    }

    public double obtenerPesoEvaluacionPorNombre(String nombre, int idAsignatura) {
        String sql = "SELECT peso FROM evaluaciones WHERE nombre = ? AND id_asignatura = ?";
        double peso = 0.0;

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setInt(2, idAsignatura);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    peso = rs.getDouble("peso");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el peso de la evaluación: " + e.getMessage());
        }

        return peso;
    }

    public int obtenerIdEvaluacionPorNombre(String nombre, int idAsignatura) {
        System.out.println("Nombre: " + nombre);
        System.out.println("ID Asignatura: " + idAsignatura);
        String sql = "SELECT id FROM evaluaciones WHERE nombre = ? AND id_asignatura = ?";
        int idEvaluacion = -1;

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setInt(2, idAsignatura);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    idEvaluacion = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener ID de evaluación: " + e.getMessage());
        }

        return idEvaluacion;
    }

}
