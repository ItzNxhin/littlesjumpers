package co.edu.udistrital.model;

import jakarta.persistence.*;

@Entity
@Table(name = "hoja_vida")
public class HojaVida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(optional = false)
    @JoinColumn(name = "estudiante_id", unique = true, nullable = false)
    private Estudiante estudiante;

    @Column(name = "estado_salud", columnDefinition = "TEXT")
    private String estadoSalud;

    @Column(columnDefinition = "TEXT")
    private String alergias;

    @Column(name = "notas_aprendizaje", columnDefinition = "TEXT")
    private String notasAprendizaje;

    // Constructors
    public HojaVida() {
    }

    public HojaVida(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public HojaVida(Estudiante estudiante, String estadoSalud, String alergias, String notasAprendizaje) {
        this.estudiante = estudiante;
        this.estadoSalud = estadoSalud;
        this.alergias = alergias;
        this.notasAprendizaje = notasAprendizaje;
    }

    // Getters and Setters
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

    public String getEstadoSalud() {
        return estadoSalud;
    }

    public void setEstadoSalud(String estadoSalud) {
        this.estadoSalud = estadoSalud;
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    public String getNotasAprendizaje() {
        return notasAprendizaje;
    }

    public void setNotasAprendizaje(String notasAprendizaje) {
        this.notasAprendizaje = notasAprendizaje;
    }
}
