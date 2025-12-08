package co.edu.udistrital.dto;

import co.edu.udistrital.model.Calificacion;

import java.math.BigDecimal;

public class CalificacionResponse {
    private Integer id;
    private Integer estudianteId;
    private String estudianteNombre;
    private Integer logroId;
    private String logroNombre;
    private String logroCategoria;
    private Integer profesorId;
    private String profesorNombre;
    private Integer periodo;
    private BigDecimal valor;

    public CalificacionResponse() {}

    public CalificacionResponse(Calificacion calificacion) {
        this.id = calificacion.getId();
        this.estudianteId = calificacion.getEstudiante().getId();
        this.estudianteNombre = calificacion.getEstudiante().getNombre() + " " + calificacion.getEstudiante().getApellido();
        this.logroId = calificacion.getLogro().getId();
        this.logroNombre = calificacion.getLogro().getNombre();
        this.logroCategoria = calificacion.getLogro().getCategoria().name();
        this.profesorId = calificacion.getProfesor().getId();
        this.profesorNombre = calificacion.getProfesor().getNombre() + " " + calificacion.getProfesor().getApellido();
        this.periodo = calificacion.getPeriodo();
        this.valor = calificacion.getValor();
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

    public Integer getLogroId() {
        return logroId;
    }

    public void setLogroId(Integer logroId) {
        this.logroId = logroId;
    }

    public String getLogroNombre() {
        return logroNombre;
    }

    public void setLogroNombre(String logroNombre) {
        this.logroNombre = logroNombre;
    }

    public String getLogroCategoria() {
        return logroCategoria;
    }

    public void setLogroCategoria(String logroCategoria) {
        this.logroCategoria = logroCategoria;
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

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
