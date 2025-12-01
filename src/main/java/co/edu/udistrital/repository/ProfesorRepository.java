package co.edu.udistrital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.model.Profesor;

@Repository
public interface ProfesorRepository extends JpaRepository<Profesor, Integer> {
    
}
