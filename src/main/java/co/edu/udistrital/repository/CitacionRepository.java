package co.edu.udistrital.repository;

import co.edu.udistrital.model.Citacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitacionRepository extends JpaRepository<Citacion, Integer> {

    /**
     * Busca todas las citaciones ordenadas por fecha de envío descendente
     */
    List<Citacion> findAllByOrderByFechaEnvioDesc();

    /**
     * Busca citaciones enviadas por un admin específico
     */
    List<Citacion> findByEnviadoPorAdminId(Integer adminId);
}
