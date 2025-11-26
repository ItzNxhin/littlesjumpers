package co.edu.udistrital.dto;

public class AcudienteRequest {
    
    private String nombre;
    private String apellido;
    private String cedula;
    private String correo;
    private String contacto_extra;
    
    public AcudienteRequest(){
    }
    
    public AcudienteRequest(String nombre, String apellido, String cedula, String correo, String contacto_extra) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.correo = correo;
        this.contacto_extra = contacto_extra;
    }

    public AcudienteRequest(String cedula) {
        this.cedula = cedula;
    }
    public AcudienteRequest(String nombre, String cedula) {
        this.nombre = nombre;
        this.cedula = cedula;
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
