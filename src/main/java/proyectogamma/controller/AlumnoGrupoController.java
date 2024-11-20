/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.controller;

/**
 *
 * @author Isabel
 */
import proyectogamma.model.AlumnoGrupos;
import proyectogamma.model.BaseDatos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlumnoGrupoController {

    // Método para obtener todos los registros de AlumnoGrupos
    public List<AlumnoGrupos> obtenerAlumnoGrupos() {
        List<AlumnoGrupos> alumnoGruposList = new ArrayList<>();
        String sql = "SELECT * FROM alumno_grupos";

        try (Connection conn = BaseDatos.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                AlumnoGrupos alumnoGrupo = new AlumnoGrupos(
                        rs.getInt("id_alumno"),
                        rs.getInt("id_grupo")
                );
                alumnoGruposList.add(alumnoGrupo);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener los registros de AlumnoGrupos: " + e.getMessage());
        }

        return alumnoGruposList;
    }

    // Método para obtener un AlumnoGrupos por id_alumno
    public AlumnoGrupos obtenerAlumnoGrupoPorIdAlumno(int idAlumno) {
        String sql = "SELECT * FROM alumno_grupos WHERE id_alumno = ?";
        AlumnoGrupos alumnoGrupo = null;

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAlumno);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    alumnoGrupo = new AlumnoGrupos(
                            rs.getInt("id_alumno"),
                            rs.getInt("id_grupo")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener AlumnoGrupo por ID de alumno: " + e.getMessage());
        }

        return alumnoGrupo;
    }

    // Método para agregar un nuevo AlumnoGrupos
    public boolean agregarAlumnoGrupo(AlumnoGrupos alumnoGrupo) {
        String sql = "INSERT INTO alumno_grupos (id_alumno, id_grupo) VALUES (?, ?)";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, alumnoGrupo.getIdAlumno());
            pstmt.setInt(2, alumnoGrupo.getIdGrupo());

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println("Error al agregar un AlumnoGrupo: " + e.getMessage());
            return false;
        }
    }

    // Método para actualizar un AlumnoGrupos existente
    public boolean actualizarAlumnoGrupo(int idAlumno, int nuevoIdGrupo) {
        String sql = "UPDATE alumno_grupos SET id_grupo = ? WHERE id_alumno = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nuevoIdGrupo);
            pstmt.setInt(2, idAlumno);

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar el AlumnoGrupo: " + e.getMessage());
            return false;
        }
    }

    // Método para eliminar un AlumnoGrupos por id_alumno
    public boolean eliminarAlumnoGrupo(int idAlumno) {
        String sql = "DELETE FROM alumno_grupos WHERE id_alumno = ?";

        try (Connection conn = BaseDatos.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAlumno);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar el AlumnoGrupo: " + e.getMessage());
            return false;
        }
    }
}
