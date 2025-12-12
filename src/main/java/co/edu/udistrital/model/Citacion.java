package co.edu.udistrital.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "citaciones")
public class Citacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCitacion tipo;

    @Column(nullable = false)
    private String asunto;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String cuerpo;

    @Column(name = "fecha_reunion", nullable = false)
    private LocalDateTime fechaReunion;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "enviado_por_admin_id", nullable = false)
    private Integer enviadoPorAdminId;

    @PrePersist
    protected void onCreate() {
        fechaEnvio = LocalDateTime.now();
    }

    public enum TipoCitacion {
        masiva,
        selectiva
    }

    // Constructors
    public Citacion() {
    }

    public Citacion(TipoCitacion tipo, String asunto, String cuerpo, LocalDateTime fechaReunion, Integer enviadoPorAdminId) {
        this.tipo = tipo;
        this.asunto = asunto;
        this.cuerpo = cuerpo;
        this.fechaReunion = fechaReunion;
        this.enviadoPorAdminId = enviadoPorAdminId;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TipoCitacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoCitacion tipo) {
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
}
