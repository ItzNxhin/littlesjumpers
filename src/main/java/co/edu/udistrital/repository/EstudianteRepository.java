package co.edu.udistrital.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.udistrital.model.Estudiante;

public interface EstudianteRepository extends JpaRepository<Estudiante, Integer>{
    
}
