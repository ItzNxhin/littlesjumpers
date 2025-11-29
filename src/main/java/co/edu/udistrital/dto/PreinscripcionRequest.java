package co.edu.udistrital.dto;

import java.time.LocalDateTime;

import co.edu.udistrital.model.Preinscripcion.EstadoEntrevista;

public class PreinscripcionRequest {

    private Integer estudiante_id;
    private LocalDateTime fecha_entrevista;
    private EstadoEntrevista estado_entrevista;

    public PreinscripcionRequest() {
    }

    public Integer getEstudiante_id() {
        return estudiante_id;
    }

    public void setEstudiante_id(Integer estudiante_id) {
        this.estudiante_id = estudiante_id;
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
}
