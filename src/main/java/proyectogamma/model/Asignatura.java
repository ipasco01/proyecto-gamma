/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.model;

public class Asignatura {
    private int id;
    private String nombre;
    private int idProfesor;
    private String nombreProfesor;

    // Constructor
    public Asignatura(int id, String nombre, int idProfesor) {
        this.id = id;
        this.nombre = nombre;
        this.idProfesor = idProfesor;
    }
    public Asignatura(int id, String nombre, int idProfesor, String nombreProfesor) {
        this.id = id;
        this.nombre = nombre;
        this.idProfesor = idProfesor;
        this.nombreProfesor = nombreProfesor;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdProfesor() {
        return idProfesor;
    }

    public void setIdProfesor(int idProfesor) {
        this.idProfesor = idProfesor;
    }
     public String getNombreProfesor() { return nombreProfesor; }
    public void setNombreProfesor(String nombreProfesor) { this.nombreProfesor = nombreProfesor; }
    // Método toString para imprimir los datos de Asignatura
    @Override
    public String toString() {
        return "Asignatura{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", idProfesor=" + idProfesor +
                '}';
    }
}