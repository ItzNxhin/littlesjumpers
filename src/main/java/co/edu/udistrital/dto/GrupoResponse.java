package co.edu.udistrital.dto;

import co.edu.udistrital.model.Grupo.Grado;

public class GrupoResponse {

    private Integer id;
    private Grado grado;
    private String identificador;
    private Integer profesor_id;
    private String profesor_nombre;
    private Integer capacidad;
    private Integer estudiantes_count;
    private String message;

    public GrupoResponse() {
    }

    public GrupoResponse(Integer id, Grado grado, String identificador) {
        this.id = id;
        this.grado = grado;
        this.identificador = identificador;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getProfesor_nombre() {
        return profesor_nombre;
    }

    public void setProfesor_nombre(String profesor_nombre) {
        this.profesor_nombre = profesor_nombre;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public Integer getEstudiantes_count() {
        return estudiantes_count;
    }

    public void setEstudiantes_count(Integer estudiantes_count) {
        this.estudiantes_count = estudiantes_count;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
