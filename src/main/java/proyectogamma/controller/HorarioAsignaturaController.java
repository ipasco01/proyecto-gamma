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

    try (Connection conn = BaseDatos.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

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


   public List<HorarioAsignatura> obtenerHorariosPorGrupo(int idGrupo) {
        String sql = """
                SELECT h.id, h.id_asignatura, h.dia, h.hora_inicio, h.hora_fin ,a.nombre AS nombreAsignatura
                FROM horarios_asignatura h 
                JOIN grupo_asignaturas ga ON h.id_asignatura = ga.id_asignatura
                JOIN asignatura a ON a.id=ga.id_asignatura
                WHERE ga.id_grupo = ?
                """;

        List<HorarioAsignatura> horarios = new ArrayList<>();

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idGrupo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HorarioAsignatura horario = new HorarioAsignatura(
                            rs.getInt("id"),
                            rs.getInt("id_asignatura"),
                            rs.getString("dia"),
                            rs.getTime("hora_inicio"),
                            rs.getTime("hora_fin"),
                            rs.getString("nombreAsignatura")
                    );
                    horarios.add(horario);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener horarios: " + e.getMessage());
        }

        return horarios;
    }

}
