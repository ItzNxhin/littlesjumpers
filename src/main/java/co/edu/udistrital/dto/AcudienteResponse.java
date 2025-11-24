package co.edu.udistrital.dto;

public class AcudienteResponse {
    
    private String nombre;
    private String apellido;
    private String cedula;
    private String correo;
    private String contacto_extra;
    private String message;


    
    public AcudienteResponse(String nombre, String apellido, String cedula, String message) {
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
    public String getContacto_extra() {
        return contacto_extra;
    }
    public void setContacto_extra(String contacto_extra) {
        this.contacto_extra = contacto_extra;
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
