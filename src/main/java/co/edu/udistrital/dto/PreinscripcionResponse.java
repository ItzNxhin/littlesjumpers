package co.edu.udistrital.dto;

import java.time.LocalDateTime;

import co.edu.udistrital.model.Preinscripcion.EstadoEntrevista;

public class PreinscripcionResponse {

    private Integer id;
    private Integer estudiante_id;
    private String estudiante_nombre;
    private String estudiante_apellido;
    private LocalDateTime fecha_solicitud;
    private LocalDateTime fecha_entrevista;
    private EstadoEntrevista estado_entrevista;
    private String message;

    public PreinscripcionResponse() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEstudiante_id() {
        return estudiante_id;
    }

    public void setEstudiante_id(Integer estudiante_id) {
        this.estudiante_id = estudiante_id;
    }

    public String getEstudiante_nombre() {
        return estudiante_nombre;
    }

    public void setEstudiante_nombre(String estudiante_nombre) {
        this.estudiante_nombre = estudiante_nombre;
    }

    public String getEstudiante_apellido() {
        return estudiante_apellido;
    }

    public void setEstudiante_apellido(String estudiante_apellido) {
        this.estudiante_apellido = estudiante_apellido;
    }

    public LocalDateTime getFecha_solicitud() {
        return fecha_solicitud;
    }

    public void setFecha_solicitud(LocalDateTime fecha_solicitud) {
        this.fecha_solicitud = fecha_solicitud;
    }

    public LocalDateTime getFecha_entrevista() {
        return fecha_entrevista;
    }

    public void setFecha_entrevista(LocalDateTime fecha_entrevista) {
        this.fecha_entrevista = fecha_entrevista;
    }

    public EstadoEntrevista getEstado_entrevista() {
        return estado_entrevista;
    }

    public void setEstado_entrevista(EstadoEntrevista estado_entrevista) {
        this.estado_entrevista = estado_entrevista;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}