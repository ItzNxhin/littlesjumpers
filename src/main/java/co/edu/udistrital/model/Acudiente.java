package co.edu.udistrital.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "acudientes")
// Al ser herrencia, el mapeo de esta columna es el mismo ID de la clase padre
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Acudiente extends Usuario {

    @Column
    private String contacto_extra;

    public String getContacto_extra() {
        return contacto_extra;
    }

    public void setContacto_extra(String contacto_extra) {
        this.contacto_extra = contacto_extra;
    }

}
