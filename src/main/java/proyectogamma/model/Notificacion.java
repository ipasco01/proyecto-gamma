/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectogamma.model;

import java.util.Date;

public class Notificacion {
    private int id;
    private String titulo;
    private String mensaje;
    private Date fechaEnvio;
    private Integer idGrupo;
    private Integer idAlumno;
    private int idDocente;
    
    private String nombreGrupo; // Nombre del grupo al que pertenece la notificación
    private String nombreAlumno; // Nombre del alumno relacionado con la notificación

    // Constructor
    public Notificacion(int id, String titulo, String mensaje, Date fechaEnvio, Integer  idAlumno, Integer  idGrupo, int idDocente) {
        this.id = id;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fechaEnvio = fechaEnvio;
        this.idAlumno = idAlumno;
        this.idGrupo = idGrupo;
        this.idDocente = idDocente;
    }
    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }

    public String getNombreAlumno() {
        return nombreAlumno;
    }

    public void setNombreAlumno(String nombreAlumno) {
        this.nombreAlumno = nombreAlumno;
    }
    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Integer getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(Integer idAlumno) {
        this.idAlumno = idAlumno;
    }

    public Integer  getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Integer idGrupo) {
        this.idGrupo = idGrupo;
    }

    public int getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(Integer idDocente) {
        this.idDocente = idDocente;
    }

    // Método toString para imprimir los datos de Notificacion
    @Override
    public String toString() {
        return "Notificacion{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", fechaEnvio=" + fechaEnvio +
                ", idAlumno=" + idAlumno +
                ", idGrupo=" + idGrupo +
                ", idDocente=" + idDocente +
                '}';
    }
}
