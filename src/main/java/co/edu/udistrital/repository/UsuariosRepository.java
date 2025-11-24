package co.edu.udistrital.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.model.Usuario;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuario, Integer>{

    Optional<Usuario> findByCedula(String cedula); 

    boolean existsByCedula(String cedula);
    
}
