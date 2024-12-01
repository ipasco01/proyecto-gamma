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
        String sql = "SELECT * FROM grupo_asignaturas";

        try (Connection conn = BaseDatos.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

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

    public String obtenerAsignaturasHTMLPorGrupo(int idGrupo) {
        StringBuilder html = new StringBuilder("<html><body>");
        String sql = "SELECT a.nombre AS asignatura "
                + "FROM grupo_asignaturas ga "
                + "INNER JOIN asignatura a ON ga.id_asignatura = a.id "
                + "WHERE ga.id_grupo = ?";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idGrupo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    html.append("- ").append(rs.getString("asignatura")).append("<br>");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener las asignaturas en formato HTML: " + e.getMessage());
        }

        html.append("</body></html>");
        return html.toString();
    }

    // Método para obtener una asignación por idGrupo e idAsignatura
    public GrupoAsignatura obtenerGrupoAsignatura(int idGrupo, int idAsignatura) {
        String sql = "SELECT * FROM grupo_asignaturas WHERE id_grupo = ? AND id_asignatura = ?";
        GrupoAsignatura grupoAsignatura = null;

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

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
        String sql = "INSERT INTO grupo_asignaturas (id_grupo, id_asignatura) VALUES (?, ?)";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

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
        String sql = "UPDATE grupo_asignaturas SET id_asignatura = ? WHERE id_grupo = ? AND id_asignatura = ?";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

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
    
    public boolean actualizarGrupoAsignaturaPorGrupo(int idGrupo, int idAsignatura, int nuevoIdGrupo) {
        String sql = "UPDATE grupo_asignaturas SET id_grupo = ? WHERE id_grupo = ? AND id_asignatura = ?";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nuevoIdGrupo);
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

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idGrupo);
            pstmt.setInt(2, idAsignatura);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar GrupoAsignatura: " + e.getMessage());
            return false;
        }
    }

    public List<GrupoAsignatura> obtenerAsignaturasConGrupo() {
        String sql = """
        SELECT a.id AS id_asignatura, 
               a.nombre AS nombre_asignatura, 
               CONCAT(d.nombre, ' ', d.apellido) AS nombreProfesor,
               IFNULL(g.nombre, 'Sin Asignar') AS nombreGrupo
        FROM asignatura a
        LEFT JOIN docente d ON a.id_profesor = d.id
        LEFT JOIN grupo_asignaturas ga ON a.id = ga.id_asignatura
        LEFT JOIN grupos g ON ga.id_grupo = g.id
        """;

        List<GrupoAsignatura> lista = new ArrayList<>();

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                GrupoAsignatura grupoAsignatura = new GrupoAsignatura(
                        rs.getInt("id_asignatura"),
                        rs.getString("nombre_asignatura"),
                        rs.getString("nombreProfesor"),
                        rs.getString("nombreGrupo")
                );
                lista.add(grupoAsignatura);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener asignaturas con grupo: " + e.getMessage());
        }

        return lista;
    }

    public List<GrupoAsignatura> obtenerAsignaturasPorNombreDeGrupo(String nombreGrupo) {
        String sql = """
        SELECT a.id AS id_asignatura, 
               a.nombre AS nombre_asignatura, 
               CONCAT(d.nombre, ' ', d.apellido) AS nombreProfesor,
               g.nombre AS nombreGrupo
        FROM asignatura a
        LEFT JOIN docente d ON a.id_profesor = d.id
        JOIN grupo_asignaturas ga ON a.id = ga.id_asignatura
        JOIN grupos g ON ga.id_grupo = g.id
        WHERE g.nombre = ?
        """;

        List<GrupoAsignatura> lista = new ArrayList<>();

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreGrupo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    GrupoAsignatura grupoAsignatura = new GrupoAsignatura(
                            rs.getInt("id_asignatura"),
                            rs.getString("nombre_asignatura"),
                            rs.getString("nombreProfesor"),
                            rs.getString("nombreGrupo")
                    );
                    lista.add(grupoAsignatura);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener asignaturas por grupo: " + e.getMessage());
        }

        return lista;
    }

}
