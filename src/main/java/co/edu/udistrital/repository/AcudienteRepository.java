package co.edu.udistrital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.model.Acudiente;

@Repository
public interface AcudienteRepository extends JpaRepository<Acudiente, Integer> {
    
}
