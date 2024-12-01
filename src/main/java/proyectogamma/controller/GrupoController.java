/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.controller;

/**
 *
 * @author Asus
 */
import proyectogamma.model.Grupos;
import proyectogamma.model.BaseDatos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GrupoController {

    public List<Grupos> obtenerGrupos() {
    String sql = "SELECT id, nombre, descripcion, fecha_creacion FROM grupos";
    List<Grupos> grupos = new ArrayList<>();

    try (Connection conn = BaseDatos.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            Grupos grupo = new Grupos(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getTimestamp("fecha_creacion")
            );
            grupos.add(grupo);
        }

    } catch (SQLException e) {
        System.out.println("Error al obtener los grupos: " + e.getMessage());
    }

    return grupos;
}

   public Integer obtenerIdGrupoPorNombre(String nombreGrupo) {
    String sql = "SELECT id FROM grupos WHERE nombre = ?";
    try (Connection conn = BaseDatos.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, nombreGrupo);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("id"); // Devuelve el ID del grupo
        } else {
            System.out.println("No se encontró el grupo con nombre: " + nombreGrupo);
            return null; // Retorna null si no se encuentra el grupo
        }
    } catch (SQLException e) {
        System.out.println("Error al obtener el ID del grupo: " + e.getMessage());
        return null; // Retorna null en caso de error
    }
}


public String obtenerNombreGrupoPorId(int idGrupo) {
    String sql = "SELECT nombre FROM grupos WHERE id = ?";
    String nombreGrupo = null;

    try (Connection conn = BaseDatos.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, idGrupo);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                nombreGrupo = rs.getString("nombre");
            } else {
                System.out.println("No se encontró un grupo con ID: " + idGrupo);
            }
        }

    } catch (SQLException e) {
        System.out.println("Error al obtener el nombre del grupo: " + e.getMessage());
    }

    return nombreGrupo;
}

    // Método para obtener un grupo por ID
    public Grupos obtenerGrupoPorId(int id) {
        String sql = "SELECT * FROM grupos WHERE id = ?";
        Grupos grupo = null;

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    grupo = new Grupos(
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
    public boolean agregarGrupo(String nombre, String descripcion) {
    String sql = "INSERT INTO grupos (nombre, descripcion, fecha_creacion) VALUES (?, ?, CURRENT_TIMESTAMP)";

    try (Connection conn = BaseDatos.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, nombre);
        pstmt.setString(2, descripcion);

        int rowsInserted = pstmt.executeUpdate();
        return rowsInserted > 0;

    } catch (SQLException e) {
        System.out.println("Error al agregar grupo: " + e.getMessage());
        return false;
    }
}


    // Método para actualizar un grupo existente
   public boolean modificarGrupo(int idGrupo, String nuevoNombre, String nuevaDescripcion) {
    String sql = "UPDATE grupos SET nombre = ?, descripcion = ? WHERE id = ?";

    try (Connection conn = BaseDatos.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, nuevoNombre);
        pstmt.setString(2, nuevaDescripcion);
        pstmt.setInt(3, idGrupo);

        int rowsUpdated = pstmt.executeUpdate();
        return rowsUpdated > 0;

    } catch (SQLException e) {
        System.out.println("Error al modificar grupo: " + e.getMessage());
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