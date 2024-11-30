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
    public boolean crearAsignaturaCompleta(String nombreAsignatura, int idProfesor, int idGrupo, String dia, Time horaInicio, Time horaFin) {
    Connection conn = null;
    PreparedStatement pstmtAsignatura = null;
    PreparedStatement pstmtGrupo = null;
    PreparedStatement pstmtHorario = null;

    String sqlAsignatura = "INSERT INTO asignatura (nombre, id_profesor) VALUES (?, ?)";
    String sqlGrupo = "INSERT INTO grupo_asignaturas (id_grupo, id_asignatura) VALUES (?, ?)";
    String sqlHorario = "INSERT INTO horarios_asignatura (id_asignatura, dia, hora_inicio, hora_fin) VALUES (?, ?, ?, ?)";

    try {
        conn = BaseDatos.getConnection();
        conn.setAutoCommit(false); // Iniciar transacción

        // 1. Insertar la asignatura
        pstmtAsignatura = conn.prepareStatement(sqlAsignatura, Statement.RETURN_GENERATED_KEYS);
        pstmtAsignatura.setString(1, nombreAsignatura);
        pstmtAsignatura.setInt(2, idProfesor);
        pstmtAsignatura.executeUpdate();

        ResultSet rs = pstmtAsignatura.getGeneratedKeys();
        int idAsignatura = 0;
        if (rs.next()) {
            idAsignatura = rs.getInt(1); // Obtener el ID generado
        }

        // 2. Asignar la asignatura al grupo
        pstmtGrupo = conn.prepareStatement(sqlGrupo);
        pstmtGrupo.setInt(1, idGrupo);
        pstmtGrupo.setInt(2, idAsignatura);
        pstmtGrupo.executeUpdate();

        // 3. Agregar el horario para la asignatura
        pstmtHorario = conn.prepareStatement(sqlHorario);
        pstmtHorario.setInt(1, idAsignatura);
        pstmtHorario.setString(2, dia);
        pstmtHorario.setTime(3, horaInicio);
        pstmtHorario.setTime(4, horaFin);
        pstmtHorario.executeUpdate();

        // Confirmar la transacción
        conn.commit();
        return true;

    } catch (SQLException e) {
        System.out.println("Error al crear asignatura completa: " + e.getMessage());
        if (conn != null) {
            try {
                conn.rollback(); // Revertir cambios si hay error
            } catch (SQLException ex) {
                System.out.println("Error al revertir la transacción: " + ex.getMessage());
            }
        }
        return false;
    } finally {
        try {
            if (pstmtAsignatura != null) pstmtAsignatura.close();
            if (pstmtGrupo != null) pstmtGrupo.close();
            if (pstmtHorario != null) pstmtHorario.close();
            if (conn != null) conn.close();
        } catch (SQLException ex) {
            System.out.println("Error al cerrar recursos: " + ex.getMessage());
        }
    }
}

    // Método para actualizar una asignatura existente
    public boolean actualizarAsignatura(int id_profesor,Asignatura asignatura) {
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
    public List<Asignatura> obtenerAsignaturasPorGrupo(int idGrupo) {
    List<Asignatura> asignaturas = new ArrayList<>();
    String sql = """
            SELECT a.id AS id_asignatura, 
                   a.nombre AS nombre_asignatura, 
                   a.id_profesor, 
                   CONCAT(p.nombre, ' ', p.apellido) AS nombreProfesor
            FROM grupo_asignaturas ga
            JOIN asignatura a ON a.id = ga.id_asignatura
            LEFT JOIN docente p ON a.id_profesor = p.id
            WHERE ga.id_grupo = ?
            """;

    try (Connection conn = BaseDatos.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, idGrupo);

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Asignatura asignatura = new Asignatura(
                    rs.getInt("id_asignatura"),
                    rs.getString("nombre_asignatura"),
                    rs.getInt("id_profesor"),
                    rs.getString("nombreProfesor")
                );
                asignaturas.add(asignatura);
            }
        }
    } catch (SQLException e) {
        System.out.println("Error al obtener asignaturas: " + e.getMessage());
    }

    return asignaturas;
}


}