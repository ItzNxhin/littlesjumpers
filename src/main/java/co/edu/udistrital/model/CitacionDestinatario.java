package co.edu.udistrital.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "citacion_destinatarios")
@IdClass(CitacionDestinatarioId.class)
public class CitacionDestinatario {

    @Id
    @Column(name = "citacion_id")
    private Integer citacionId;

    @Id
    @Column(name = "usuario_id")
    private Integer usuarioId;

    @ManyToOne
    @JoinColumn(name = "citacion_id", insertable = false, updatable = false)
    private Citacion citacion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private Usuario usuario;

    // Constructors
    public CitacionDestinatario() {
    }

    public CitacionDestinatario(Integer citacionId, Integer usuarioId) {
        this.citacionId = citacionId;
        this.usuarioId = usuarioId;
    }

    // Getters and Setters
    public Integer getCitacionId() {
        return citacionId;
    }

    public void setCitacionId(Integer citacionId) {
        this.citacionId = citacionId;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Citacion getCitacion() {
        return citacion;
    }

    public void setCitacion(Citacion citacion) {
        this.citacion = citacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
