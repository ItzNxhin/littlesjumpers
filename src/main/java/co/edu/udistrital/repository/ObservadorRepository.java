package co.edu.udistrital.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.model.Estudiante;
import co.edu.udistrital.model.Observador;

@Repository
public interface ObservadorRepository extends JpaRepository<Observador, Integer> {

    /**
     * Busca todas las observaciones de un estudiante, ordenadas por fecha descendente
     */
    List<Observador> findByEstudianteOrderByFechaDesc(Estudiante estudiante);

    /**
     * Busca todas las observaciones de un estudiante por ID
     */
    List<Observador> findByEstudianteIdOrderByFechaDesc(Integer estudianteId);
}
