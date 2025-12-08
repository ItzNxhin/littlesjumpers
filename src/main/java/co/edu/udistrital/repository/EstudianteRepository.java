package co.edu.udistrital.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.udistrital.model.Estudiante;
import co.edu.udistrital.model.Grupo;
import co.edu.udistrital.model.Estudiante.Estado;

import java.util.List;
import java.util.Optional;


public interface EstudianteRepository extends JpaRepository<Estudiante, Integer>{

    Optional<List<Estudiante>> findByEstado(Estado estado);

    List<Estudiante> findByGrupo(Grupo grupo);

    List<Estudiante> findByGrupoId(Integer grupoId);
}
