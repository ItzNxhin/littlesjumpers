package co.edu.udistrital.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "profesores")
// Al ser herrencia, el mapeo de esta columna es el mismo ID de la clase padre
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Profesor extends Usuario {

    @Column
    private String tarjeta_profesional;

    public String getTarjeta_profesional() {
        return tarjeta_profesional;
    }

    public void setTarjeta_profesional(String tarjeta_profesional) {
        this.tarjeta_profesional = tarjeta_profesional;
    }

}