package co.edu.udistrital.dto;

import co.edu.udistrital.model.Usuario;

public class UsuarioSimpleResponse {
    private Integer id;
    private String nombre;
    private String apellido;
    private String correo;
    private String nombreCompleto;

    public UsuarioSimpleResponse() {
    }

    public UsuarioSimpleResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.correo = usuario.getCorreo();
        this.nombreCompleto = usuario.getNombre() + " " + usuario.getApellido();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
}
