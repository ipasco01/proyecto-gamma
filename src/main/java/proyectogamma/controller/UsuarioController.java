/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.controller;
import proyectogamma.model.Usuario;
import proyectogamma.model.BaseDatos;
import proyectogamma.model.Alumno;
import proyectogamma.controller.AlumnoController;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import proyectogamma.model.Docente;

/**
 *
 * @author Asus
 */
public class UsuarioController {

    // Método para autenticar al usuario (login)
    public Usuario login(String nombreUsuario, String contrasena) {
        String sql = "SELECT * FROM usuario WHERE nombre_usuario = ? AND contrasena = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreUsuario);
            pstmt.setString(2, contrasena);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Crear un objeto Usuario con los datos obtenidos
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre_usuario"),
                        rs.getString("contrasena"),
                        rs.getString("rol"),
                        rs.getObject("id_alumno") != null ? rs.getInt("id_alumno") : null,
                        rs.getObject("id_docente") != null ? rs.getInt("id_docente") : null,
                        rs.getObject("id_padre") != null ? rs.getInt("id_padre") : null
                );
            }

        } catch (SQLException e) {
            System.out.println("Error al autenticar usuario: " + e.getMessage());
        }

        return null; 
    }

    // Método para agregar un nuevo usuario
    public boolean agregarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuario (nombre_usuario, contrasena, rol, id_alumno, id_docente, id_padre) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNombreUsuario());
            pstmt.setString(2, usuario.getContrasena()); // En un sistema real, usa encriptación
            pstmt.setString(3, usuario.getRol());
            pstmt.setObject(4, usuario.getIdAlumno());
            pstmt.setObject(5, usuario.getIdDocente());
            pstmt.setObject(6, usuario.getIdPadre());

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println("Error al agregar usuario: " + e.getMessage());
            return false;
        }
    }

    // Método para actualizar un usuario existente
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuario SET nombre_usuario = ?, contrasena = ?, rol = ?, id_alumno = ?, id_docente = ?, id_padre = ? WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNombreUsuario());
            pstmt.setString(2, usuario.getContrasena());
            pstmt.setString(3, usuario.getRol());
            pstmt.setObject(4, usuario.getIdAlumno());
            pstmt.setObject(5, usuario.getIdDocente());
            pstmt.setObject(6, usuario.getIdPadre());
            pstmt.setInt(7, usuario.getId());

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    // Método para eliminar un usuario por ID
    public boolean eliminarUsuario(int id) {
        String sql = "DELETE FROM usuario WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
    public boolean cambiarContrasena(int idUsuario, String nuevaContrasena) {
    String sql = "UPDATE usuario SET contrasena = ? WHERE id = ?";

    try (Connection conn = BaseDatos.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        // Encripta la contraseña antes de almacenarla (opcional)
        pstmt.setString(1, nuevaContrasena);
        pstmt.setInt(2, idUsuario);

        int rowsUpdated = pstmt.executeUpdate();
        return rowsUpdated > 0;

    } catch (SQLException e) {
        System.out.println("Error al cambiar la contraseña: " + e.getMessage());
        return false;
    }
}

    // Método para obtener un usuario por ID
    public Usuario obtenerUsuarioPorId(int id) {
        String sql = "SELECT * FROM usuario WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre_usuario"),
                        rs.getString("contrasena"),
                        rs.getString("rol"),
                        rs.getObject("id_alumno") != null ? rs.getInt("id_alumno") : null,
                        rs.getObject("id_docente") != null ? rs.getInt("id_docente") : null,
                        rs.getObject("id_padre") != null ? rs.getInt("id_padre") : null
                );
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener usuario: " + e.getMessage());
        }

        return null;
    }
    public Alumno obtenerAlumnoPorUsuario(Usuario usuario) {
    String sql = """
        SELECT a.id, a.nombre, a.apellido, a.email, a.fecha_nacimiento, a.fecha_registro
        FROM usuario u
        JOIN alumno a ON u.id_alumno = a.id
        WHERE u.id = ?;
    """;

    Alumno alumno = null;

    try (Connection conn = BaseDatos.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, usuario.getId()); 
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            alumno = new Alumno(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("email"),
                    rs.getDate("fecha_nacimiento"),
                    rs.getTimestamp("fecha_registro")
            );
        } else {
            System.out.println("No se encontró un alumno asociado al usuario con ID: " + usuario.getId());
        }

    } catch (SQLException e) {
        System.out.println("Error al obtener datos del alumno asociado: " + e.getMessage());
    }

    return alumno;
}

public Docente obtenerDocentePorUsuario(Usuario usuario) {
    String sql = """
            SELECT a.id, a.nombre, a.apellido, a.email, a.especialidad, a.fecha_contratacion, a.fecha_registro
        FROM usuario u
        JOIN docente a ON u.id_docente = a.id
        WHERE u.id = ?;
    """;

    Docente docente = null;

    try (Connection conn = BaseDatos.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, usuario.getId()); 
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            docente = new Docente(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("email"),
                    rs.getString("especialidad"),
                    rs.getDate("fecha_contratacion"),
                    rs.getTimestamp("fecha_registro")
            );
        } else {
            System.out.println("No se encontró un alumno asociado al usuario con ID: " + usuario.getId());
        }

    } catch (SQLException e) {
        System.out.println("Error al obtener datos del alumno asociado: " + e.getMessage());
    }

    return docente;
}

}
