package co.edu.udistrital.dto;

public class ProfesorResponse {

    private String nombre;
    private String apellido;
    private String cedula;
    private String correo;
    private String tarjeta_profesional;
    private String message;

    public ProfesorResponse(String nombre, String apellido, String cedula, String message) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTarjeta_profesional() {
        return tarjeta_profesional;
    }

    public void setTarjeta_profesional(String tarjeta_profesional) {
        this.tarjeta_profesional = tarjeta_profesional;
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

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
