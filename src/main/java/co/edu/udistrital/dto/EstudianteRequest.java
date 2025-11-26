package co.edu.udistrital.dto;

import java.time.LocalDate;

import co.edu.udistrital.model.Estudiante.Estado;
import co.edu.udistrital.model.Grupo;
import co.edu.udistrital.model.Grupo.Grado;

public class EstudianteRequest {

    private int id;
    private String tarjeta_identidad;
    private String nombre;
    private String apellido;
    private LocalDate fecha_nacimiento;
    private Grado grado_aplicado;
    private Estado estado;
    private Grupo grupo;
    private String acudiente_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTarjeta_identidad() {
        return tarjeta_identidad;
    }

    public void setTarjeta_identidad(String tarjeta_identidad) {
        this.tarjeta_identidad = tarjeta_identidad;
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

    public LocalDate getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(LocalDate fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public Grado getGrado_aplicado() {
        return grado_aplicado;
    }

    public void setGrado_aplicado(Grado grado_aplicado) {
        this.grado_aplicado = grado_aplicado;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public String getAcudiente_id() {
        return acudiente_id;
    }

    public void setAcudiente_id(String acudiente_id) {
        this.acudiente_id = acudiente_id;
    }

}