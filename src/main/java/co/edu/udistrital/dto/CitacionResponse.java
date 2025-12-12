package co.edu.udistrital.dto;

import co.edu.udistrital.model.Citacion;

import java.time.LocalDateTime;
import java.util.List;

public class CitacionResponse {
    private Integer id;
    private String tipo;
    private String asunto;
    private String cuerpo;
    private LocalDateTime fechaReunion;
    private LocalDateTime fechaEnvio;
    private Integer enviadoPorAdminId;
    private Integer cantidadDestinatarios;

    public CitacionResponse() {
    }

    public CitacionResponse(Citacion citacion) {
        this.id = citacion.getId();
        this.tipo = citacion.getTipo().name();
        this.asunto = citacion.getAsunto();
        this.cuerpo = citacion.getCuerpo();
        this.fechaReunion = citacion.getFechaReunion();
        this.fechaEnvio = citacion.getFechaEnvio();
        this.enviadoPorAdminId = citacion.getEnviadoPorAdminId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public LocalDateTime getFechaReunion() {
        return fechaReunion;
    }

    public void setFechaReunion(LocalDateTime fechaReunion) {
        this.fechaReunion = fechaReunion;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Integer getEnviadoPorAdminId() {
        return enviadoPorAdminId;
    }

    public void setEnviadoPorAdminId(Integer enviadoPorAdminId) {
        this.enviadoPorAdminId = enviadoPorAdminId;
    }

    public Integer getCantidadDestinatarios() {
        return cantidadDestinatarios;
    }

    public void setCantidadDestinatarios(Integer cantidadDestinatarios) {
        this.cantidadDestinatarios = cantidadDestinatarios;
    }
}
