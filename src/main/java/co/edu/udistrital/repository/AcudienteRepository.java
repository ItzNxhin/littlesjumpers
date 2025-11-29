package co.edu.udistrital.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.model.Acudiente;

@Repository
public interface AcudienteRepository extends JpaRepository<Acudiente, Integer> {

    /**
     * Busca un acudiente por su número de cédula
     * @param cedula Número de cédula del acudiente
     * @return Optional con el acudiente si existe
     */
    Optional<Acudiente> findByCedula(String cedula);

}
