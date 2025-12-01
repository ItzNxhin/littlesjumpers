package co.edu.udistrital.dto;

import co.edu.udistrital.model.Cuenta;

import java.time.LocalDateTime;

public class CuentaResponse {
    private int id;
    private String nombreUsuario;
    private String rol;
    private boolean activo;
    private LocalDateTime fechaCreacion;

    public CuentaResponse() {}

    public CuentaResponse(Cuenta cuenta) {
        this.id = cuenta.getId();
        this.nombreUsuario = cuenta.getNombreUsuario();
        this.rol = cuenta.getRol() != null ? cuenta.getRol().name() : null;
        this.activo = cuenta.isActivo();
        this.fechaCreacion = cuenta.getFechaCreacion();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
