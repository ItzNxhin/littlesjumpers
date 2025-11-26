package co.edu.udistrital.model;

import java.time.LocalDate;

import co.edu.udistrital.model.Grupo.Grado;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "estudiantes")
public class Estudiante {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "cedula")
    private String tarjeta_identidad;

    @Column
    private String nombre;

    @Column
    private String apellido;

    @Column
    private LocalDate fecha_nacimiento;

    @Column
    private Grado grado_aplicado;

    @Column
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "grupo_id", nullable = true)
    private Grupo grupo;

    @ManyToOne
    @JoinColumn(name = "acudiente_id", nullable = false)
    private Acudiente acudiente;

    public enum Estado {
        aspirante,
        aceptado,
        rechazado
    }

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

    public Acudiente getAcudiente() {
        return acudiente;
    }

    public void setAcudiente(Acudiente acudiente) {
        this.acudiente = acudiente;
    }

    

}
