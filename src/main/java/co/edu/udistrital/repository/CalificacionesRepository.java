package co.edu.udistrital.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.udistrital.model.Calificacion;
import co.edu.udistrital.model.Estudiante;

import java.util.List;


public interface CalificacionesRepository extends JpaRepository<Calificacion, Integer> {
    
    List<Calificacion> findByEstudiante(Estudiante estudiante);
}
