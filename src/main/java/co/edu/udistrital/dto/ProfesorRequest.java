package co.edu.udistrital.dto;

public class ProfesorRequest {

    private String nombre;
    private String apellido;
    private String cedula;
    private String correo;
    private String tarjeta_profesional;

    public ProfesorRequest() {
    }

    public ProfesorRequest(String nombre, String apellido, String cedula, String correo, String tarjeta_profesional) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.correo = correo;
        this.tarjeta_profesional = tarjeta_profesional;
    }

    public ProfesorRequest(String cedula) {
        this.cedula = cedula;
    }

    public ProfesorRequest(String nombre, String cedula) {
        this.nombre = nombre;
        this.cedula = cedula;
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
