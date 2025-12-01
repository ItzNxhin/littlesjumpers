package co.edu.udistrital.dto;

public class AsignarCuentaRequest {
    private int cuentaId;
    private int usuarioId;
    private String tipoUsuario; // "profesor" o "acudiente"

    public AsignarCuentaRequest() {}

    public AsignarCuentaRequest(int cuentaId, int usuarioId, String tipoUsuario) {
        this.cuentaId = cuentaId;
        this.usuarioId = usuarioId;
        this.tipoUsuario = tipoUsuario;
    }

    public int getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(int cuentaId) {
        this.cuentaId = cuentaId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
