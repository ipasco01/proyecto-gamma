/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.model;

public class GrupoAsignatura {
    private int idGrupo;
    private int idAsignatura;

    // Constructor
    public GrupoAsignatura(int idGrupo, int idAsignatura) {
        this.idGrupo = idGrupo;
        this.idAsignatura = idAsignatura;
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

    // Método toString para imprimir los datos de GrupoAsignatura
    @Override
    public String toString() {
        return "GrupoAsignatura{" +
                "idGrupo=" + idGrupo +
                ", idAsignatura=" + idAsignatura +
                '}';
    }
}