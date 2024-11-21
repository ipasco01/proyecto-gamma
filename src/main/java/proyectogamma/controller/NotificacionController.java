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
public List<Notificacion> obtenerNotificacionesConDetallesPorDocente(int idDocente) {
    List<Notificacion> notificaciones = new ArrayList<>();
    String sql = """
    SELECT n.id, n.titulo, n.mensaje, n.fecha_envio, 
           n.id_alumno, n.id_grupo, n.id_docente,
           g.nombre AS nombre_grupo, 
           a.nombre AS nombre_alumno, a.apellido AS apellido_alumno
    FROM notificacion n
    LEFT JOIN grupos g ON n.id_grupo = g.id
    LEFT JOIN alumno a ON n.id_alumno = a.id
    WHERE n.id_docente = ?
""";


    try (Connection conn = BaseDatos.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, idDocente);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Integer idAlumno = null; // Usamos Integer para permitir valores null
            Object idAlumnoObject = rs.getObject("id_alumno");
            if (idAlumnoObject != null) {
                idAlumno = (Integer) idAlumnoObject;
            }
            Integer idGrupo = null; // Usamos Integer para permitir valores null
            Object idGrupoObject = rs.getObject("id_grupo");
            if (idGrupoObject != null) {
                idGrupo = (Integer) idGrupoObject;
            }

            Notificacion notificacion = new Notificacion(
                    rs.getInt("id"),
                    rs.getString("titulo"),
                    rs.getString("mensaje"),
                    rs.getTimestamp("fecha_envio"),
                    idAlumno,
                    idGrupo,
                    idDocente
            );
            // Añade los nombres del grupo y alumno como propiedades adicionales (si las necesitas)
            String nombreGrupo = rs.getString("nombre_grupo");
            notificacion.setNombreGrupo(nombreGrupo != null ? nombreGrupo : "Sin Grupo");
            String nombreAlumno = rs.getString("nombre_alumno");
            String apellidoAlumno = rs.getString("apellido_alumno");
            notificacion.setNombreAlumno(
                    (nombreAlumno != null ? nombreAlumno : "") + " " + 
                            (apellidoAlumno != null ? apellidoAlumno : "")
            );

            notificaciones.add(notificacion);
        }

    } catch (SQLException e) {
        System.out.println("Error al obtener notificaciones con detalles: " + e.getMessage());
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
            pstmt.setObject(4, notificacion.getIdAlumno(), java.sql.Types.INTEGER);
            pstmt.setObject(5, notificacion.getIdGrupo(), java.sql.Types.INTEGER);
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
            pstmt.setObject(4, notificacion.getIdAlumno(), java.sql.Types.INTEGER);
            pstmt.setObject(5, notificacion.getIdGrupo(), java.sql.Types.INTEGER);
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