package co.edu.udistrital.dto;

public class ObservacionRequest {

    private Integer estudianteId;
    private Integer profesorId;
    private String texto;

    public ObservacionRequest() {
    }

    public ObservacionRequest(Integer estudianteId, Integer profesorId, String texto) {
        this.estudianteId = estudianteId;
        this.profesorId = profesorId;
        this.texto = texto;
    }

    public Integer getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Integer estudianteId) {
        this.estudianteId = estudianteId;
    }

    public Integer getProfesorId() {
        return profesorId;
    }

    public void setProfesorId(Integer profesorId) {
        this.profesorId = profesorId;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
