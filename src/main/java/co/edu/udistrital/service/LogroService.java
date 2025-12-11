package co.edu.udistrital.service;

import co.edu.udistrital.model.Logro;
import co.edu.udistrital.repository.LogroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LogroService {

    @Autowired
    private LogroRepository logroRepository;

    /**
     * Obtiene todos los logros
     */
    @Transactional(readOnly = true)
    public List<Logro> obtenerTodos() {
        try {
            return logroRepository.findAll();
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al consultar logros", e);
        }
    }

    /**
     * Obtiene un logro por ID
     */
    @Transactional(readOnly = true)
    public Optional<Logro> obtenerPorId(Integer id) {
        try {
            return logroRepository.findById(id);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al consultar logro", e);
        }
    }

    /**
     * Crea un nuevo logro
     */
    @Transactional
    public Logro crear(Logro logro) {
        try {
            return logroRepository.save(logro);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al crear logro", e);
        }
    }

    /**
     * Actualiza un logro existente
     */
    @Transactional
    public Logro actualizar(Integer id, Logro logro) {
        try {
            Optional<Logro> existente = logroRepository.findById(id);
            if (existente.isEmpty()) {
                return null;
            }

            Logro logroActualizar = existente.get();
            logroActualizar.setNombre(logro.getNombre());
            logroActualizar.setCategoria(logro.getCategoria());

            return logroRepository.save(logroActualizar);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al actualizar logro", e);
        }
    }

    /**
     * Elimina un logro
     */
    @Transactional
    public boolean eliminar(Integer id) {
        try {
            if (!logroRepository.existsById(id)) {
                return false;
            }
            logroRepository.deleteById(id);
            return true;
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al eliminar logro", e);
        }
    }
}
