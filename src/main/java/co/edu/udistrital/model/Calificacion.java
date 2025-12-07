package co.edu.udistrital.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "calificaciones")
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "logro_id", nullable = false)
    private Logro logro;

    @ManyToOne
    @JoinColumn(name = "profesor_id", nullable = false)
    private Profesor profesor;

    @Column(nullable = false)
    private Integer periodo;

    @Column(nullable = false, precision = 3, scale = 1)
    private BigDecimal valor;

    public Calificacion() {
    }

    public Calificacion(Estudiante estudiante, Logro logro, Profesor profesor, Integer periodo, BigDecimal valor) {
        this.estudiante = estudiante;
        this.logro = logro;
        this.profesor = profesor;
        this.periodo = periodo;
        this.valor = valor;
    }

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

    public Logro getLogro() {
        return logro;
    }

    public void setLogro(Logro logro) {
        this.logro = logro;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
