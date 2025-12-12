package co.edu.udistrital.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CitacionRequest {
    private String tipo; // "masiva" o "selectiva"
    private String asunto;
    private String cuerpo;
    private LocalDateTime fechaReunion;
    private List<Integer> destinatariosIds; // Solo para citaciones selectivas

    public CitacionRequest() {
    }

    public CitacionRequest(String tipo, String asunto, String cuerpo, LocalDateTime fechaReunion, List<Integer> destinatariosIds) {
        this.tipo = tipo;
        this.asunto = asunto;
        this.cuerpo = cuerpo;
        this.fechaReunion = fechaReunion;
        this.destinatariosIds = destinatariosIds;
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

    public List<Integer> getDestinatariosIds() {
        return destinatariosIds;
    }

    public void setDestinatariosIds(List<Integer> destinatariosIds) {
        this.destinatariosIds = destinatariosIds;
    }
}
