package co.edu.udistrital.model;

import jakarta.persistence.*;

@Entity
@Table(name = "hoja_vida")
public class HojaVida {

    @Id
    @Column(name = "estudiante_id")
    private Integer estudianteId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "estudiante_id")
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
        this.estudianteId = estudiante.getId();
    }

    public HojaVida(Estudiante estudiante, String estadoSalud, String alergias, String notasAprendizaje) {
        this.estudiante = estudiante;
        this.estudianteId = estudiante.getId();
        this.estadoSalud = estadoSalud;
        this.alergias = alergias;
        this.notasAprendizaje = notasAprendizaje;
    }

    // Getters and Setters
    public Integer getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Integer estudianteId) {
        this.estudianteId = estudianteId;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
        this.estudianteId = estudiante.getId();
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
