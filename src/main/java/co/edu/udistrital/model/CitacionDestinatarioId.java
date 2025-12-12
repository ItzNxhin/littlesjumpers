package co.edu.udistrital.model;

import java.io.Serializable;
import java.util.Objects;

public class CitacionDestinatarioId implements Serializable {
    private Integer citacionId;
    private Integer usuarioId;

    public CitacionDestinatarioId() {
    }

    public CitacionDestinatarioId(Integer citacionId, Integer usuarioId) {
        this.citacionId = citacionId;
        this.usuarioId = usuarioId;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CitacionDestinatarioId that = (CitacionDestinatarioId) o;
        return Objects.equals(citacionId, that.citacionId) &&
               Objects.equals(usuarioId, that.usuarioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(citacionId, usuarioId);
    }
}
