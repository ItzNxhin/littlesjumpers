package co.edu.udistrital.repository;

import co.edu.udistrital.model.Logro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogroRepository extends JpaRepository<Logro, Integer> {
}
