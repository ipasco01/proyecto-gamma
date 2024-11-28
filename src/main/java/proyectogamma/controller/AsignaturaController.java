/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.controller;

/**
 *
 * @author Isabel
 */
import proyectogamma.model.Asignatura;
import proyectogamma.model.BaseDatos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsignaturaController {

    // Método para obtener todas las asignaturas
    public List<Asignatura> obtenerAsignaturas() {
        List<Asignatura> asignaturas = new ArrayList<>();
        String sql = "SELECT * FROM asignatura";

        try (Connection conn = BaseDatos.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Asignatura asignatura = new Asignatura(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("id_profesor")
                );
                asignaturas.add(asignatura);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener asignaturas: " + e.getMessage());
        }

        return asignaturas;
    }
    public List<Asignatura> obtenerAsignaturasPorDocente(int idDocente) {
    List<Asignatura> asignaturas = new ArrayList<>();
    String sql = "SELECT id, nombre FROM asignatura WHERE id_profesor = ?";

    try (Connection conn = BaseDatos.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, idDocente);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Asignatura asignatura = new Asignatura(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    idDocente
            );
            asignaturas.add(asignatura);
        }

    } catch (SQLException e) {
        System.out.println("Error al obtener asignaturas del docente: " + e.getMessage());
    }

    return asignaturas;
}

    // Método para obtener una asignatura por ID
    public Asignatura obtenerAsignaturaPorId(int id) {
        String sql = "SELECT * FROM asignatura WHERE id = ?";
        Asignatura asignatura = null;

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    asignatura = new Asignatura(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getInt("id_profesor")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener la asignatura: " + e.getMessage());
        }

        return asignatura;
    }

    // Método para agregar una nueva asignatura
    public boolean agregarAsignatura(Asignatura asignatura) {
        String sql = "INSERT INTO asignatura (nombre, id_profesor) VALUES (?, ?)";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, asignatura.getNombre());
            pstmt.setInt(2, asignatura.getIdProfesor());

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println("Error al agregar asignatura: " + e.getMessage());
            return false;
        }
    }

    // Método para actualizar una asignatura existente
    public boolean actualizarAsignatura(Asignatura asignatura) {
        String sql = "UPDATE asignatura SET nombre = ?, id_profesor = ? WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, asignatura.getNombre());
            pstmt.setInt(2, asignatura.getIdProfesor());
            pstmt.setInt(3, asignatura.getId());

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar asignatura: " + e.getMessage());
            return false;
        }
    }

    // Método para eliminar una asignatura por ID
    public boolean eliminarAsignatura(int id) {
        String sql = "DELETE FROM asignatura WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar asignatura: " + e.getMessage());
            return false;
        }
    }
}