package co.edu.udistrital.dto;

import java.math.BigDecimal;

public class CalificacionRequest {
    private Integer estudianteId;
    private Integer logroId;
    private Integer profesorId;
    private Integer periodo;
    private BigDecimal valor;

    public CalificacionRequest() {}

    public CalificacionRequest(Integer estudianteId, Integer logroId, Integer profesorId, Integer periodo, BigDecimal valor) {
        this.estudianteId = estudianteId;
        this.logroId = logroId;
        this.profesorId = profesorId;
        this.periodo = periodo;
        this.valor = valor;
    }

    public Integer getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Integer estudianteId) {
        this.estudianteId = estudianteId;
    }

    public Integer getLogroId() {
        return logroId;
    }

    public void setLogroId(Integer logroId) {
        this.logroId = logroId;
    }

    public Integer getProfesorId() {
        return profesorId;
    }

    public void setProfesorId(Integer profesorId) {
        this.profesorId = profesorId;
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
