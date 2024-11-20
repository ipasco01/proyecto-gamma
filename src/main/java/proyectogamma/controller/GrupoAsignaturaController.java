/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.controller;

/**
 *
 * @author Isabel
 */
import proyectogamma.model.GrupoAsignatura;
import proyectogamma.model.BaseDatos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GrupoAsignaturaController {

    // Método para obtener todas las asignaciones de GrupoAsignatura
    public List<GrupoAsignatura> obtenerGrupoAsignaturas() {
        List<GrupoAsignatura> grupoAsignaturas = new ArrayList<>();
        String sql = "SELECT * FROM grupo_asignatura";

        try (Connection conn = BaseDatos.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                GrupoAsignatura grupoAsignatura = new GrupoAsignatura(
                        rs.getInt("id_grupo"),
                        rs.getInt("id_asignatura")
                );
                grupoAsignaturas.add(grupoAsignatura);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener los registros de GrupoAsignatura: " + e.getMessage());
        }

        return grupoAsignaturas;
    }

    // Método para obtener una asignación por idGrupo e idAsignatura
    public GrupoAsignatura obtenerGrupoAsignatura(int idGrupo, int idAsignatura) {
        String sql = "SELECT * FROM grupo_asignatura WHERE id_grupo = ? AND id_asignatura = ?";
        GrupoAsignatura grupoAsignatura = null;

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idGrupo);
            pstmt.setInt(2, idAsignatura);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    grupoAsignatura = new GrupoAsignatura(
                            rs.getInt("id_grupo"),
                            rs.getInt("id_asignatura")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener GrupoAsignatura: " + e.getMessage());
        }

        return grupoAsignatura;
    }

    // Método para agregar una nueva asignación de GrupoAsignatura
    public boolean agregarGrupoAsignatura(GrupoAsignatura grupoAsignatura) {
        String sql = "INSERT INTO grupo_asignatura (id_grupo, id_asignatura) VALUES (?, ?)";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, grupoAsignatura.getIdGrupo());
            pstmt.setInt(2, grupoAsignatura.getIdAsignatura());

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println("Error al agregar GrupoAsignatura: " + e.getMessage());
            return false;
        }
    }

    // Método para actualizar una asignación existente
    public boolean actualizarGrupoAsignatura(int idGrupo, int idAsignatura, int nuevoIdAsignatura) {
        String sql = "UPDATE grupo_asignatura SET id_asignatura = ? WHERE id_grupo = ? AND id_asignatura = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nuevoIdAsignatura);
            pstmt.setInt(2, idGrupo);
            pstmt.setInt(3, idAsignatura);

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar GrupoAsignatura: " + e.getMessage());
            return false;
        }
    }

    // Método para eliminar una asignación de GrupoAsignatura
    public boolean eliminarGrupoAsignatura(int idGrupo, int idAsignatura) {
        String sql = "DELETE FROM grupo_asignatura WHERE id_grupo = ? AND id_asignatura = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idGrupo);
            pstmt.setInt(2, idAsignatura);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar GrupoAsignatura: " + e.getMessage());
            return false;
        }
    }
}