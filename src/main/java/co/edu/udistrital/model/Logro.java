package co.edu.udistrital.model;

import jakarta.persistence.*;

@Entity
@Table(name = "logros")
public class Logro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaLogro categoria;

    public enum CategoriaLogro {
        psicosocial,
        psicomotor,
        cognitivo,
        procedimental
    }

    public Logro() {
    }

    public Logro(String nombre, CategoriaLogro categoria) {
        this.nombre = nombre;
        this.categoria = categoria;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public CategoriaLogro getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaLogro categoria) {
        this.categoria = categoria;
    }
}
