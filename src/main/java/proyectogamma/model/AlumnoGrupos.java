/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.model;

public class AlumnoGrupos {
    private int id;
    private int idGrupo;

    // Constructor
    public AlumnoGrupos(int id, int idGrupo) {
        this.id = id;
        this.idGrupo = idGrupo;
    }

    // Getters y Setters
    public int getIdAlumno() {
        return id;
    }

    public void setIdAlumno(int idAlumno) {
        this.id = idAlumno;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    // Método toString para imprimir los datos de AlumnoGrupos
    @Override
    public String toString() {
        return "AlumnoGrupos{" +
                "idAlumno=" + id +
                ", idGrupo=" + idGrupo +
                '}';
    }
}
