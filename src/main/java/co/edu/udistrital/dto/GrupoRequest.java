package co.edu.udistrital.dto;

import co.edu.udistrital.model.Grupo.Grado;

public class GrupoRequest {

    private Grado grado;
    private String identificador;
    private Integer profesor_id;
    private Integer capacidad;

    public GrupoRequest() {
    }

    public GrupoRequest(Grado grado, String identificador, Integer profesor_id, Integer capacidad) {
        this.grado = grado;
        this.identificador = identificador;
        this.profesor_id = profesor_id;
        this.capacidad = capacidad;
    }

    public Grado getGrado() {
        return grado;
    }

    public void setGrado(Grado grado) {
        this.grado = grado;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public Integer getProfesor_id() {
        return profesor_id;
    }

    public void setProfesor_id(Integer profesor_id) {
        this.profesor_id = profesor_id;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }
}
