/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.controller;

/**
 *
 * @author iSABEL
 */
import proyectogamma.model.Notificacion;
import proyectogamma.model.BaseDatos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificacionController {

    // Método para obtener todas las notificaciones
    public List<Notificacion> obtenerNotificaciones() {
        List<Notificacion> notificaciones = new ArrayList<>();
        String sql = "SELECT * FROM notificacion";

        try (Connection conn = BaseDatos.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Notificacion notificacion = new Notificacion(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("mensaje"),
                        rs.getTimestamp("fecha_envio"),
                        rs.getInt("id_alumno"),
                        rs.getInt("id_grupo"),
                        rs.getInt("id_docente")
                );
                notificaciones.add(notificacion);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener notificaciones: " + e.getMessage());
        }

        return notificaciones;
    }
    public List<Notificacion> obtenerNotificacionesPorGrupo(int idGrupo) {
    List<Notificacion> notificaciones = new ArrayList<>();
    String sql = "SELECT * FROM notificacion WHERE id_grupo = ?";

    try (Connection conn = BaseDatos.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, idGrupo);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Notificacion notificacion = new Notificacion(
                    rs.getInt("id"),
                    rs.getString("titulo"),
                    rs.getString("mensaje"),
                    rs.getTimestamp("fecha_envio"),
                    rs.getInt("id_alumno"),
                    rs.getInt("id_grupo"),
                    rs.getInt("id_docente")
            );
            notificaciones.add(notificacion);
        }

    } catch (SQLException e) {
        System.out.println("Error al obtener notificaciones grupales: " + e.getMessage());
    }

    return notificaciones;
}
    public List<Notificacion> obtenerNotificacionesPorAlumno(int idAlumno) {
    List<Notificacion> notificaciones = new ArrayList<>();
    String sql = "SELECT * FROM notificacion WHERE id_alumno = ?";

    try (Connection conn = BaseDatos.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, idAlumno);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Notificacion notificacion = new Notificacion(
                    rs.getInt("id"),
                    rs.getString("titulo"),
                    rs.getString("mensaje"),
                    rs.getTimestamp("fecha_envio"),
                    rs.getInt("id_alumno"),
                    rs.getInt("id_grupo"),
                    rs.getInt("id_docente")
            );
            notificaciones.add(notificacion);
        }

    } catch (SQLException e) {
        System.out.println("Error al obtener notificaciones grupales: " + e.getMessage());
    }

    return notificaciones;
}

    // Método para obtener una notificación por ID
    public Notificacion obtenerNotificacionPorId(int id) {
        String sql = "SELECT * FROM notificacion WHERE id = ?";
        Notificacion notificacion = null;

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    notificacion = new Notificacion(
                            rs.getInt("id"),
                            rs.getString("titulo"),
                            rs.getString("mensaje"),
                            rs.getTimestamp("fecha_envio"),
                            rs.getInt("id_alumno"),
                            rs.getInt("id_grupo"),
                            rs.getInt("id_docente")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener la notificación: " + e.getMessage());
        }

        return notificacion;
    }

    // Método para agregar una nueva notificación
    public boolean agregarNotificacion(Notificacion notificacion) {
        String sql = "INSERT INTO notificacion (titulo, mensaje, fecha_envio, id_alumno, id_grupo, id_docente) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, notificacion.getTitulo());
            pstmt.setString(2, notificacion.getMensaje());
            pstmt.setTimestamp(3, new Timestamp(notificacion.getFechaEnvio().getTime()));
            pstmt.setInt(4, notificacion.getIdAlumno());
            pstmt.setInt(5, notificacion.getIdGrupo());
            pstmt.setInt(6, notificacion.getIdDocente());

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println("Error al agregar notificación: " + e.getMessage());
            return false;
        }
    }

    // Método para actualizar una notificación existente
    public boolean actualizarNotificacion(Notificacion notificacion) {
        String sql = "UPDATE notificacion SET titulo = ?, mensaje = ?, fecha_envio = ?, id_alumno = ?, id_grupo = ?, id_docente = ? " +
                     "WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, notificacion.getTitulo());
            pstmt.setString(2, notificacion.getMensaje());
            pstmt.setTimestamp(3, new Timestamp(notificacion.getFechaEnvio().getTime()));
            pstmt.setInt(4, notificacion.getIdAlumno());
            pstmt.setInt(5, notificacion.getIdGrupo());
            pstmt.setInt(6, notificacion.getIdDocente());
            pstmt.setInt(7, notificacion.getId());

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar notificación: " + e.getMessage());
            return false;
        }
    }

    // Método para eliminar una notificación por ID
    public boolean eliminarNotificacion(int id) {
        String sql = "DELETE FROM notificacion WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar notificación: " + e.getMessage());
            return false;
        }
    }
}