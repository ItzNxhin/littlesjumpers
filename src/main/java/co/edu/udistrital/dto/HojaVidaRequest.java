package co.edu.udistrital.dto;

public class HojaVidaRequest {
    private String estadoSalud;
    private String alergias;
    private String notasAprendizaje;

    public HojaVidaRequest() {
    }

    public HojaVidaRequest(String estadoSalud, String alergias, String notasAprendizaje) {
        this.estadoSalud = estadoSalud;
        this.alergias = alergias;
        this.notasAprendizaje = notasAprendizaje;
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
