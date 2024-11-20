/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.controller;

/**
 *
 * @author Asus
 */
import proyectogamma.model.Grupo;
import proyectogamma.model.BaseDatos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GrupoController {

    // Método para obtener todos los grupos
    public List<Grupo> obtenerGrupos() {
        List<Grupo> grupos = new ArrayList<>();
        String sql = "SELECT * FROM grupos";

        try (Connection conn = BaseDatos.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Grupo grupo = new Grupo(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getTimestamp("fecha_creacion")
                );
                grupos.add(grupo);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener grupos: " + e.getMessage());
        }

        return grupos;
    }

    // Método para obtener un grupo por ID
    public Grupo obtenerGrupoPorId(int id) {
        String sql = "SELECT * FROM grupos WHERE id = ?";
        Grupo grupo = null;

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    grupo = new Grupo(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getTimestamp("fecha_creacion")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener el grupo: " + e.getMessage());
        }

        return grupo;
    }

    // Método para agregar un nuevo grupo
    public boolean agregarGrupo(Grupo grupo) {
        String sql = "INSERT INTO grupos (nombre, descripcion, fecha_creacion) VALUES (?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, grupo.getNombre());
            pstmt.setString(2, grupo.getDescripcion());

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println("Error al agregar el grupo: " + e.getMessage());
            return false;
        }
    }

    // Método para actualizar un grupo existente
    public boolean actualizarGrupo(Grupo grupo) {
        String sql = "UPDATE grupos SET nombre = ?, descripcion = ? WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, grupo.getNombre());
            pstmt.setString(2, grupo.getDescripcion());
            pstmt.setInt(3, grupo.getId());

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar el grupo: " + e.getMessage());
            return false;
        }
    }

    // Método para eliminar un grupo por ID
    public boolean eliminarGrupo(int id) {
        String sql = "DELETE FROM grupos WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar el grupo: " + e.getMessage());
            return false;
        }
    }
}