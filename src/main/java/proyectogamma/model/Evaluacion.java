/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.model;

import java.util.Date;

/**
 *
 * @author Isabel
 */
public class Evaluacion {
    private int id;
    private String nombre;
    private double peso;
    private int idAsignatura;
    private Date fecha;

    // Constructor
    public Evaluacion(int id, String nombre, double peso, int idAsignatura, Date fecha) {
        this.id = id;
        this.nombre = nombre;
        this.peso = peso;
        this.idAsignatura = idAsignatura;
        this.fecha = fecha;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
    public int getIdAsignatura() { return idAsignatura; }
    public void setIdAsignatura(int idAsignatura) { this.idAsignatura = idAsignatura; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}
