package co.edu.udistrital.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.model.Estudiante;
import co.edu.udistrital.model.Preinscripcion;
import co.edu.udistrital.model.Preinscripcion.EstadoEntrevista;

@Repository
public interface PreinscripcionRepository extends JpaRepository<Preinscripcion, Integer> {

    /**
     * Buscar preinscripción por estudiante
     */
    Optional<Preinscripcion> findByEstudiante(Estudiante estudiante);

    /**
     * Buscar preinscripción por ID de estudiante
     */
    Optional<Preinscripcion> findByEstudianteId(Integer estudianteId);

    /**
     * Buscar todas las preinscripciones por estado de entrevista
     * Nota: El nombre del método debe coincidir con el campo de la entidad (estado_entrevista)
     */
    List<Preinscripcion> findByEstado(EstadoEntrevista estado_entrevista);

    /**
     * Verificar si existe preinscripción para un estudiante
     */
    boolean existsByEstudiante(Estudiante estudiante);

    /**
     * Verificar si existe preinscripción para un estudiante por ID
     */
    boolean existsByEstudianteId(Integer estudianteId);
}
