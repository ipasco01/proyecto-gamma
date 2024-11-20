/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.controller;

import proyectogamma.model.Alumno;
import proyectogamma.model.BaseDatos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import proyectogamma.model.Usuario;
public class AlumnoController {

    // Método para obtener todos los alumnos
    public List<Alumno> obtenerAlumnos() {
        List<Alumno> alumnos = new ArrayList<>();
        String sql = "SELECT * FROM alumno";

        try (Connection conn = BaseDatos.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Alumno alumno = new Alumno(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        rs.getDate("fecha_nacimiento"),
                        rs.getTimestamp("fecha_registro")
                );
                alumnos.add(alumno);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener alumnos: " + e.getMessage());
        }

        return alumnos;
    }

    public Alumno obtenerAlumnoPorId(int id) {
    String sql = "SELECT * FROM alumno WHERE id = ?";
    Alumno alumno = null;

    try (Connection conn = BaseDatos.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        System.out.println("Conexión establecida: " + !conn.isClosed()); // Verificar si la conexión está activa
        pstmt.setInt(1, id);

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                alumno = new Alumno(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        rs.getDate("fecha_nacimiento"),
                        rs.getTimestamp("fecha_registro")
                );
            }
        }
    } catch (SQLException e) {
        System.out.println("Error al obtener alumno por ID: " + e.getMessage());
    }

    if (alumno == null) {
        System.out.println("No se encontró un alumno con ID: " + id);
    }

    return alumno;
}

    public boolean agregarAlumno(Alumno alumno, String nombreUsuario, String contrasena, int idGrupo) {
    String sqlAlumno = "INSERT INTO alumno (nombre, apellido, email, fecha_nacimiento, fecha_registro) " +
                       "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
    String sqlAsignarGrupo = "INSERT INTO alumno_grupos (id_alumno, id_grupo) VALUES (?, ?)";

    Connection conn = null;
    PreparedStatement pstmtAlumno = null;
    PreparedStatement pstmtAsignarGrupo = null;

    try {
        conn = BaseDatos.getConnection();
        conn.setAutoCommit(false); // Iniciar transacción

        // Insertar el alumno
        pstmtAlumno = conn.prepareStatement(sqlAlumno, Statement.RETURN_GENERATED_KEYS);
        pstmtAlumno.setString(1, alumno.getNombre());
        pstmtAlumno.setString(2, alumno.getApellido());
        pstmtAlumno.setString(3, alumno.getEmail());
        pstmtAlumno.setDate(4, new java.sql.Date(alumno.getFechaNacimiento().getTime()));

        int rowsInsertedAlumno = pstmtAlumno.executeUpdate();
        if (rowsInsertedAlumno == 0) {
            throw new SQLException("Fallo al insertar el alumno.");
        }

        // Obtener el ID del alumno recién insertado
        ResultSet generatedKeys = pstmtAlumno.getGeneratedKeys();
        if (!generatedKeys.next()) {
            throw new SQLException("No se pudo obtener el ID del alumno.");
        }
        int idAlumno = generatedKeys.getInt(1);

        // Crear el usuario asociado al alumno
        UsuarioController usuarioController = new UsuarioController();
        Usuario usuario = new Usuario(0, nombreUsuario, contrasena, "Alumno", idAlumno, null, null);

        if (!usuarioController.agregarUsuario(usuario)) {
            throw new SQLException("Fallo al insertar el usuario asociado al alumno.");
        }

        // Asignar al alumno al grupo
        pstmtAsignarGrupo = conn.prepareStatement(sqlAsignarGrupo);
        pstmtAsignarGrupo.setInt(1, idAlumno);
        pstmtAsignarGrupo.setInt(2, idGrupo);

        int rowsInsertedGrupo = pstmtAsignarGrupo.executeUpdate();
        if (rowsInsertedGrupo == 0) {
            throw new SQLException("Fallo al asignar el alumno al grupo.");
        }

        conn.commit(); // Confirmar la transacción
        return true;

    } catch (SQLException e) {
        System.out.println("Error al agregar alumno, usuario y asignar a grupo: " + e.getMessage());
        if (conn != null) {
            try {
                conn.rollback(); // Revertir la transacción en caso de error
            } catch (SQLException rollbackEx) {
                System.out.println("Error al hacer rollback: " + rollbackEx.getMessage());
            }
        }
        return false;

    } finally {
        try {
            if (pstmtAlumno != null) pstmtAlumno.close();
            if (pstmtAsignarGrupo != null) pstmtAsignarGrupo.close();
            if (conn != null) conn.close();
        } catch (SQLException closeEx) {
            System.out.println("Error al cerrar recursos: " + closeEx.getMessage());
        }
    }
}


    // Método para actualizar un alumno existente
    public boolean actualizarAlumno(Alumno alumno) {
        String sql = "UPDATE alumno SET nombre = ?, apellido = ?, email = ?, fecha_nacimiento = ? " +
                     "WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, alumno.getNombre());
            pstmt.setString(2, alumno.getApellido());
            pstmt.setString(3, alumno.getEmail());
            pstmt.setDate(4, new java.sql.Date(alumno.getFechaNacimiento().getTime()));
            pstmt.setInt(5, alumno.getId());

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar alumno: " + e.getMessage());
            return false;
        }
    }

    // Método para eliminar un alumno por ID
    public boolean eliminarAlumno(int id) {
        String sql = "DELETE FROM alumno WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar alumno: " + e.getMessage());
            return false;
        }
    }public boolean asignarAlumnoAGrupo(int idAlumno, int idGrupo) {
        String sql = "INSERT INTO alumno_grupos (id_alumno, id_grupo) VALUES (?, ?)";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAlumno);
            pstmt.setInt(2, idGrupo);

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println("Error al asignar alumno a grupo: " + e.getMessage());
            return false;
        }
    }

    public int obtenerIdGrupoPorAlumno(int idAlumno) {
    String sql = "SELECT id_grupo FROM alumno_grupos WHERE id_alumno = ?";
    int idGrupo = -1;

    try (Connection conn = BaseDatos.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, idAlumno);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            idGrupo = rs.getInt("id_grupo");
        }

    } catch (SQLException e) {
        System.out.println("Error al obtener el grupo del alumno: " + e.getMessage());
    }
    return idGrupo;}
}
