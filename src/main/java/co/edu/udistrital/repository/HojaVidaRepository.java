package co.edu.udistrital.repository;

import co.edu.udistrital.model.HojaVida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HojaVidaRepository extends JpaRepository<HojaVida, Integer> {

    /**
     * Busca la hoja de vida por el ID del estudiante
     */
    Optional<HojaVida> findByEstudianteId(Integer estudianteId);
}
