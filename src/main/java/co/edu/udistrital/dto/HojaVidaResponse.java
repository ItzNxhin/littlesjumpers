package co.edu.udistrital.dto;

import co.edu.udistrital.model.HojaVida;

public class HojaVidaResponse {
    private Integer estudianteId;
    private String estudianteNombre;
    private String estadoSalud;
    private String alergias;
    private String notasAprendizaje;

    public HojaVidaResponse() {
    }

    public HojaVidaResponse(HojaVida hojaVida) {
        this.estudianteId = hojaVida.getEstudiante().getId();
        this.estudianteNombre = hojaVida.getEstudiante().getNombre() + " " + hojaVida.getEstudiante().getApellido();
        this.estadoSalud = hojaVida.getEstadoSalud();
        this.alergias = hojaVida.getAlergias();
        this.notasAprendizaje = hojaVida.getNotasAprendizaje();
    }

    public Integer getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Integer estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getEstudianteNombre() {
        return estudianteNombre;
    }

    public void setEstudianteNombre(String estudianteNombre) {
        this.estudianteNombre = estudianteNombre;
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
