/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.model;

import java.util.Date;

public class Calificacion {
    private int id;
    private int idAlumno;
    private int idAsignatura;
    private double nota;
    private Date fecha;
    private int idExamen;


    // Constructor
    public Calificacion(int id, int idAlumno, int idAsignatura, double nota,int idExamen, Date fecha) {
        this.id = id;
        this.idAlumno = idAlumno;
        this.idAsignatura = idAsignatura;   
        this.nota = nota;
        this.idExamen = idExamen;
        this.fecha = fecha;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(int idAlumno) {
        this.idAlumno = idAlumno;
    }

    public int getIdAsignatura() {
        return idAsignatura;
    }

    public void setIdAsignatura(int idAsignatura) {
        this.idAsignatura = idAsignatura;
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    public int getIdExamen() { return idExamen; }
public void setIdExamen(int idExamen) { this.idExamen = idExamen; }

    // Método toString para imprimir los datos de Calificacion
    @Override
    public String toString() {
        return "Calificacion{" +
                "id=" + id +
                ", idAlumno=" + idAlumno +
                ", idAsignatura=" + idAsignatura +
                ", nota=" + nota +
                ", idExamen=" + idExamen +
                ", fecha=" + fecha +
                '}';
    }
}
