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
import proyectogamma.model.Calificacion;
import proyectogamma.model.Usuario;

public class AlumnoController {

    // Método para obtener todos los alumnos
    public List<Alumno> obtenerAlumnos() {
        List<Alumno> alumnos = new ArrayList<>();
        String sql = "SELECT * FROM alumno";

        try (Connection conn = BaseDatos.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

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

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

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

    public Integer obtenerIdAlumnoPorNombre(String nombreCompleto) {
        String sql = "SELECT id FROM alumno WHERE CONCAT(nombre, ' ', apellido) = ?";
        Integer idAlumno = null;

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreCompleto);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                idAlumno = rs.getInt("id");
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener ID del alumno por nombre completo: " + e.getMessage());
        }

        return idAlumno;
    }

    public List<Alumno> obtenerAlumnosOrdenadosPorApellido(int idGrupo) {
        List<Alumno> alumnos = new ArrayList<>();
        String sql = "SELECT a.id, a.nombre, a.apellido, a.email, a.fecha_nacimiento "
                + "FROM alumno a "
                + "INNER JOIN alumno_grupos ag ON a.id = ag.id_alumno "
                + "WHERE ag.id_grupo = ? "
                + "ORDER BY a.apellido ASC"; // Ordenar por apellido

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idGrupo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Alumno alumno = new Alumno(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getDate("fecha_nacimiento"),
                            null
                    );
                    alumnos.add(alumno);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener alumnos ordenados por apellido: " + e.getMessage());
        }

        return alumnos;
    }

    public boolean agregarAlumno(Alumno alumno, String nombreUsuario, String contrasena, int idGrupo) {
        String sqlAlumno = "INSERT INTO alumno (nombre, apellido, email, fecha_nacimiento, fecha_registro) "
                + "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
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
                if (pstmtAlumno != null) {
                    pstmtAlumno.close();
                }
                if (pstmtAsignarGrupo != null) {
                    pstmtAsignarGrupo.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException closeEx) {
                System.out.println("Error al cerrar recursos: " + closeEx.getMessage());
            }
        }
    }

    public boolean agregarAlumnoConGrupo(Alumno alumno, int idGrupo) {
        String sqlAlumno = "INSERT INTO alumno (nombre, apellido, email, fecha_nacimiento, fecha_registro) VALUES (?, ?, ?, NULL, CURRENT_TIMESTAMP)";
        String sqlGrupo = "INSERT INTO alumno_grupos (id_alumno, id_grupo) VALUES (?, ?)";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmtAlumno = conn.prepareStatement(sqlAlumno, Statement.RETURN_GENERATED_KEYS); PreparedStatement pstmtGrupo = conn.prepareStatement(sqlGrupo)) {

            // Insertar alumno
            pstmtAlumno.setString(1, alumno.getNombre());
            pstmtAlumno.setString(2, alumno.getApellido());
            pstmtAlumno.setString(3, alumno.getEmail());
            int filasAlumno = pstmtAlumno.executeUpdate();

            if (filasAlumno == 0) {
                throw new SQLException("No se pudo insertar el alumno.");
            }

            // Obtener el ID del alumno recién insertado
            ResultSet rs = pstmtAlumno.getGeneratedKeys();
            if (!rs.next()) {
                throw new SQLException("No se pudo obtener el ID del alumno.");
            }
            int idAlumno = rs.getInt(1);

            // Asignar alumno al grupo
            pstmtGrupo.setInt(1, idAlumno);
            pstmtGrupo.setInt(2, idGrupo);
            int filasGrupo = pstmtGrupo.executeUpdate();

            if (filasGrupo == 0) {
                throw new SQLException("No se pudo asignar el alumno al grupo.");
            }

            // Crear el usuario para el alumno
            String nombreUsuario = alumno.getNombre().toLowerCase().replaceAll(" ", "") + alumno.getApellido().toLowerCase().replaceAll(" ", "");
            String contrasena = nombreUsuario + "123";

            UsuarioController usuarioController = new UsuarioController();
            Usuario usuario = new Usuario(0, nombreUsuario, contrasena, "Alumno", idAlumno, null, null);

            if (!usuarioController.agregarUsuario(usuario)) {
                throw new SQLException("No se pudo crear el usuario para el alumno.");
            }

            return true;

        } catch (SQLException e) {
            System.out.println("Error al agregar alumno con grupo y usuario: " + e.getMessage());
            return false;
        }
    }

    public String obtenerNombreAlumnoPorId(int idAlumno) {
        String sql = "SELECT nombre, apellido FROM alumno WHERE id = ?";
        String nombreCompleto = null;

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAlumno);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("nombre");
                    String apellido = rs.getString("apellido");
                    nombreCompleto = nombre + " " + apellido;
                } else {
                    System.out.println("No se encontró un alumno con ID: " + idAlumno);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener el nombre del alumno: " + e.getMessage());
        }

        return nombreCompleto;
    }

    public List<String> obtenerAsignaturasPorGrupo(int idGrupo) {
        List<String> asignaturas = new ArrayList<>();
        String sql = """
        SELECT a.nombre
        FROM grupo_asignaturas ga
        JOIN asignatura a ON ga.id_asignatura = a.id
        WHERE ga.id_grupo = ?
    """;

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idGrupo);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                asignaturas.add(rs.getString("nombre"));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener asignaturas por grupo: " + e.getMessage());
        }

        return asignaturas;
    }

    public List<String[]> obtenerNotasPorAsignatura(int idAlumno, String nombreAsignatura) {
        List<String[]> notas = new ArrayList<>();
        String sql = """
        SELECT c.nota, c.fecha
        FROM calificacion c
        JOIN asignatura a ON c.id_asignatura = a.id
        WHERE c.id_alumno = ? AND a.nombre = ?
    """;

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAlumno);
            pstmt.setString(2, nombreAsignatura);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String nota = String.valueOf(rs.getDouble("nota"));
                String fecha = rs.getString("fecha");
                notas.add(new String[]{nota, fecha});
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener notas del alumno: " + e.getMessage());
        }

        return notas;
    }

    public boolean actualizarAlumno(Alumno alumno) {
        String sql = "UPDATE alumno SET nombre = ?, apellido = ?, email = ? "
                + (alumno.getFechaNacimiento() != null ? ", fecha_nacimiento = ? " : "") // Incluir fecha si no es null
                + "WHERE id = ?";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, alumno.getNombre());
            pstmt.setString(2, alumno.getApellido());
            pstmt.setString(3, alumno.getEmail());

            int parameterIndex = 4; // Posición del siguiente parámetro

            if (alumno.getFechaNacimiento() != null) {
                pstmt.setDate(parameterIndex++, new java.sql.Date(alumno.getFechaNacimiento().getTime()));
            }

            pstmt.setInt(parameterIndex, alumno.getId());

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

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar alumno: " + e.getMessage());
            return false;
        }
    }

    public boolean asignarAlumnoAGrupo(int idAlumno, int idGrupo) {
        String sql = "INSERT INTO alumno_grupos (id_alumno, id_grupo) VALUES (?, ?)";

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

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

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAlumno);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                idGrupo = rs.getInt("id_grupo");
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener el grupo del alumno: " + e.getMessage());
        }
        return idGrupo;
    }

    public List<Alumno> obtenerAlumnosPorAsignatura(int idAsignatura) {
        List<Alumno> alumnos = new ArrayList<>();
        String sql = """
            SELECT a.id, a.nombre, a.apellido, a.email
            FROM alumno a
            JOIN alumno_grupos ag ON a.id = ag.id_alumno
            JOIN grupo_asignaturas ga ON ag.id_grupo = ga.id_grupo
            WHERE ga.id_asignatura = ?
        """;

        try (Connection conn = BaseDatos.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAsignatura);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Alumno alumno = new Alumno(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        null,
                        null
                );
                alumnos.add(alumno);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener alumnos por asignatura: " + e.getMessage());
        }

        return alumnos;
    }
}
