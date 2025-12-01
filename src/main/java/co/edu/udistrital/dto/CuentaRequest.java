package co.edu.udistrital.dto;

public class CuentaRequest {
    private String nombreUsuario;
    private String contrasena;
    private String tipoRol; // "acudiente", "profesor", "admin"

    public CuentaRequest() {}

    public CuentaRequest(String nombreUsuario, String contrasena, String tipoRol) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.tipoRol = tipoRol;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getTipoRol() {
        return tipoRol;
    }

    public void setTipoRol(String tipoRol) {
        this.tipoRol = tipoRol;
    }
}
