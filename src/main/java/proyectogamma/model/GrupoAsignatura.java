/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.model;

public class GrupoAsignatura {
    private int idGrupo;
    private int idAsignatura;
    private String nombreAsignatura;
    private String nombreProfesor;
    private String nombreGrupo;

    // Constructor
    public GrupoAsignatura(int idGrupo, int idAsignatura) {
        this.idGrupo = idGrupo;
        this.idAsignatura = idAsignatura;
    }
    public GrupoAsignatura(int idAsignatura, String nombreAsignatura, String nombreProfesor, String nombreGrupo) {
        this.idAsignatura = idAsignatura;
        this.nombreAsignatura = nombreAsignatura;
        this.nombreProfesor = nombreProfesor;
        this.nombreGrupo = nombreGrupo;
    }

    // Getters y Setters
    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public int getIdAsignatura() {
        return idAsignatura;
    }

    public void setIdAsignatura(int idAsignatura) {
        this.idAsignatura = idAsignatura;
    }
    public String getNombreAsignatura() {
        return nombreAsignatura;
    }

    public void setNombreAsignatura(String nombreAsignatura) {
        this.nombreAsignatura = nombreAsignatura;
    }

    public String getNombreProfesor() {
        return nombreProfesor;
    }

    public void setNombreProfesor(String nombreProfesor) {
        this.nombreProfesor = nombreProfesor;
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }

    // Método toString para imprimir los datos de GrupoAsignatura
    @Override
    public String toString() {
        return "GrupoAsignatura{" +
                "idGrupo=" + idGrupo +
                ", idAsignatura=" + idAsignatura +
                '}';
    }
}