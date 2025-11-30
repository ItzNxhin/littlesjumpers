package co.edu.udistrital.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "preinscripciones")
public class Preinscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "estudiante_id", unique = true, nullable = false)
    private Estudiante estudiante;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fecha_solicitud;

    @Column(name = "fecha_entrevista")
    private LocalDateTime fecha_entrevista;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_entrevista", nullable = false)
    private EstadoEntrevista estado;

    public EstadoEntrevista getEstado() {
        return estado;
    }

    public void setEstado(EstadoEntrevista estado) {
        this.estado = estado;
    }

    public enum EstadoEntrevista {
        pendiente,
        programada,
        realizada
    }

    // Constructor por defecto
    public Preinscripcion() {
        this.fecha_solicitud = LocalDateTime.now();
        this.estado = EstadoEntrevista.pendiente;
    }

    //Constructor con estudiante
    public Preinscripcion(Estudiante estudiante) {
        this.estudiante =  estudiante;
        this.fecha_solicitud = LocalDateTime.now();
        this.estado = EstadoEntrevista.pendiente;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public LocalDateTime getFecha_solicitud() {
        return fecha_solicitud;
    }

    public void setFecha_solicitud(LocalDateTime fecha_solicitud) {
        this.fecha_solicitud = fecha_solicitud;
    }

    public LocalDateTime getFecha_entrevista() {
        return fecha_entrevista;
    }

    public void setFecha_entrevista(LocalDateTime fecha_entrevista) {
        this.fecha_entrevista = fecha_entrevista;
    }

    
}
