package co.edu.udistrital.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.model.Grupo;
import co.edu.udistrital.model.Grupo.Grado;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Integer> {

    /**
     * Busca grupos por grado
     */
    List<Grupo> findByGrado(Grado grado);

    /**
     * Cuenta cuántos estudiantes tiene un grupo
     */
    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.grupo.id = :grupoId")
    Long contarEstudiantesPorGrupo(Integer grupoId);

    /**
     * Busca grupos por profesor
     */
    List<Grupo> findByProfesorId(Integer profesorId);
}
