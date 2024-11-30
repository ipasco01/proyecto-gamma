/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.model;


import java.sql.Time;

public class HorarioAsignatura {
    private int id;
    private int idAsignatura;
    private String dia;
    private Time horaInicio;
    private Time horaFin;
    private String nombreAsignatura;
    private String nombreGrupo;

    // Constructor
    public HorarioAsignatura(int id, int idAsignatura, String dia, Time horaInicio, Time horaFin,String nombreAsignatura) {
        this.id = id;
        this.idAsignatura = idAsignatura;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.nombreAsignatura = nombreAsignatura;
    }
public HorarioAsignatura(int id, int idAsignatura, String dia, Time horaInicio, Time horaFin,String nombreAsignatura,String nombreGrupo) {
        this.id = id;
        this.idAsignatura = idAsignatura;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.nombreAsignatura = nombreAsignatura;
        this.nombreGrupo= nombreGrupo;
    }
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdAsignatura() { return idAsignatura; }
    public void setIdAsignatura(int idAsignatura) { this.idAsignatura = idAsignatura; }
    public String getDia() { return dia; }
    public void setDia(String dia) { this.dia = dia; }
    public String getNombreAsignatura() { return nombreAsignatura; }
    public void setNombreAsignatura(String nombreAsignatura) { this.nombreAsignatura = nombreAsignatura; }
    public Time getHoraInicio() { return horaInicio; }
    public void setHoraInicio(Time horaInicio) { this.horaInicio = horaInicio; }
    public Time getHoraFin() { return horaFin; }
    public void setHoraFin(Time horaFin) { this.horaFin = horaFin; }

    public String getNombreGrupo() {
        return nombreGrupo;
    }
    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo= nombreGrupo;
    }
}
