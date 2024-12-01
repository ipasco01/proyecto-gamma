/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import proyectogamma.model.BaseDatos;
import proyectogamma.model.HorarioAsignatura;

public class HorarioAsignaturaController {

    public List<HorarioAsignatura> obtenerHorariosPorProfesor(int idProfesor) {
        String sql = """
            SELECT h.id, h.id_asignatura, h.dia, h.hora_inicio, h.hora_fin, a.nombre AS nombreAsignatura,g.nombre AS nombreGrupo
            FROM horarios_asignatura h
            JOIN asignatura a ON h.id_asignatura = a.id
            INNER JOIN grupo_asignaturas ga ON a.id=ga.id_grupo
            INNER JOIN grupos g ON g.id=ga.id_grupo
            WHERE a.id_profesor = ?
            """;

        List<HorarioAsignatura> horarios = new ArrayList<>();

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProfesor);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HorarioAsignatura horario = new HorarioAsignatura(
                            rs.getInt("id"),
                            rs.getInt("id_asignatura"),
                            rs.getString("dia"),
                            rs.getTime("hora_inicio"),
                            rs.getTime("hora_fin"),
                            rs.getString("nombreAsignatura"),
                            rs.getString("nombreGrupo")
                    );
                    horarios.add(horario);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener horarios del profesor: " + e.getMessage());
        }

        return horarios;
    }

    public boolean eliminarHorario(int idHorario) {
        String sql = "DELETE FROM horarios_asignatura WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idHorario);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar horario: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarHorariosPorIdAsignatura(int idAsignatura) {
        String sql = "DELETE FROM horarios_asignatura WHERE id_asignatura = ?";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAsignatura);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar horarios: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarHorario(HorarioAsignatura horario) {
        String sql = "UPDATE horarios_asignatura SET dia = ?, hora_inicio = ?, hora_fin = ? WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, horario.getDia());
            pstmt.setTime(2, horario.getHoraInicio());
            pstmt.setTime(3, horario.getHoraFin());
            pstmt.setInt(4, horario.getId());

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar horario: " + e.getMessage());
            return false;
        }
    }

    public List<HorarioAsignatura> obtenerHorariosPorIdAsignatura(int idAsignatura) {
        String sql = "SELECT * FROM horarios_asignatura WHERE id_asignatura = ?";
        List<HorarioAsignatura> horarios = new ArrayList<>();

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAsignatura);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                horarios.add(new HorarioAsignatura(
                        rs.getInt("id"),
                        rs.getInt("id_asignatura"),
                        rs.getString("dia"),
                        rs.getTime("hora_inicio"),
                        rs.getTime("hora_fin"),
                        "" // El nombre de la asignatura no es necesario aquí
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener horarios: " + e.getMessage());
        }

        return horarios;
    }

    public List<HorarioAsignatura> obtenerHorariosPorGrupo(int idGrupo) {
        String sql = """
                SELECT h.id, h.id_asignatura, h.dia, h.hora_inicio, h.hora_fin ,a.nombre AS nombreAsignatura,d.nombre AS nombreProfesor
                FROM horarios_asignatura h 
                JOIN grupo_asignaturas ga ON h.id_asignatura = ga.id_asignatura
                JOIN asignatura a ON a.id=ga.id_asignatura
                JOIN docente d ON d.id=a.id_profesor
                WHERE ga.id_grupo = ?
                """;

        List<HorarioAsignatura> horarios = new ArrayList<>();

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idGrupo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HorarioAsignatura horario = new HorarioAsignatura(
                            rs.getInt("id"),
                            rs.getInt("id_asignatura"),
                            rs.getString("dia"),
                            rs.getTime("hora_inicio"),
                            rs.getTime("hora_fin"),
                            rs.getString("nombreAsignatura"),
                            rs.getString("nombreProfesor")
                    );
                    horarios.add(horario);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener horarios: " + e.getMessage());
        }

        return horarios;
    }

    public boolean agregarHorario(HorarioAsignatura horario) {
        String sql = "INSERT INTO horarios_asignatura (id_asignatura, dia, hora_inicio, hora_fin) VALUES (?, ?, ?, ?)";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, horario.getIdAsignatura());
            pstmt.setString(2, horario.getDia());
            pstmt.setTime(3, horario.getHoraInicio());
            pstmt.setTime(4, horario.getHoraFin());

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println("Error al agregar horario: " + e.getMessage());
            return false;
        }
    }

}
