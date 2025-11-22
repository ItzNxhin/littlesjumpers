package co.edu.udistrital.model;

public class Usuario {

    private Cuenta Cuenta;
    private String nombre;
    private String apellido;
    private int id_usuario;
    private String cedula;
    private String correo;


    public Cuenta getCuenta() {
        return Cuenta;
    }
    public void setCuenta(Cuenta cuenta) {
        Cuenta = cuenta;
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
    public int getId_usuario() {
        return id_usuario;
    }
    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
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
