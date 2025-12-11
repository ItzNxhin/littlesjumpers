package co.edu.udistrital.dto;

import java.time.LocalDateTime;

import co.edu.udistrital.model.Observador;

public class ObservacionResponse {

    private Integer id;
    private Integer estudianteId;
    private String estudianteNombre;
    private Integer profesorId;
    private String profesorNombre;
    private String texto;
    private LocalDateTime fecha;

    public ObservacionResponse() {
    }

    public ObservacionResponse(Observador observacion) {
        this.id = observacion.getId();
        this.estudianteId = observacion.getEstudiante().getId();
        this.estudianteNombre = observacion.getEstudiante().getNombre() + " " + observacion.getEstudiante().getApellido();
        this.profesorId = observacion.getProfesor().getId();
        this.profesorNombre = observacion.getProfesor().getNombre() + " " + observacion.getProfesor().getApellido();
        this.texto = observacion.getTexto();
        this.fecha = observacion.getFecha();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Integer estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getEstudianteNombre() {
        return estudianteNombre;
    }

    public void setEstudianteNombre(String estudianteNombre) {
        this.estudianteNombre = estudianteNombre;
    }

    public Integer getProfesorId() {
        return profesorId;
    }

    public void setProfesorId(Integer profesorId) {
        this.profesorId = profesorId;
    }

    public String getProfesorNombre() {
        return profesorNombre;
    }

    public void setProfesorNombre(String profesorNombre) {
        this.profesorNombre = profesorNombre;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
