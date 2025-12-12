package co.edu.udistrital.repository;

import co.edu.udistrital.model.CitacionDestinatario;
import co.edu.udistrital.model.CitacionDestinatarioId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitacionDestinatarioRepository extends JpaRepository<CitacionDestinatario, CitacionDestinatarioId> {

    /**
     * Busca todos los destinatarios de una citación específica
     */
    List<CitacionDestinatario> findByCitacionId(Integer citacionId);

    /**
     * Busca todas las citaciones de un usuario específico
     */
    List<CitacionDestinatario> findByUsuarioId(Integer usuarioId);
}
