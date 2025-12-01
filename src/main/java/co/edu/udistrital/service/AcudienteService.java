package co.edu.udistrital.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.dto.AcudienteRequest;
import co.edu.udistrital.dto.AcudienteResponse;
import co.edu.udistrital.dto.mapper.AcudienteEntityMapper;
import co.edu.udistrital.exception.DatabaseException;
import co.edu.udistrital.model.Acudiente;
import co.edu.udistrital.repository.AcudienteRepository;

@Service
public class AcudienteService {

    @Autowired
    private AcudienteRepository acudienteRepository;

    /**
     * Obtiene todos los acudientes
     * @return Lista de AcudienteResponse
     */
    @Transactional(readOnly = true)
    public List<AcudienteResponse> obtenerTodos() {
        try {
            List<Acudiente> acudientes = acudienteRepository.findAll();
            return AcudienteEntityMapper.toResponseList(acudientes);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al consultar acudientes", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener acudientes: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene un acudiente por su ID
     * @param id ID del acudiente
     * @return AcudienteResponse si existe, null si no existe
     */
    @Transactional(readOnly = true)
    public AcudienteResponse obtenerPorId(Integer id) {
        try {
            Optional<Acudiente> acudiente = acudienteRepository.findById(id);
            if (acudiente.isEmpty()) {
                return null;
            }
            return AcudienteEntityMapper.toResponse(acudiente.get());
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al consultar acudiente por ID", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener acudiente: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene un acudiente por su cédula
     * @param cedula Cédula del acudiente
     * @return AcudienteResponse si existe, null si no existe
     */
    @Transactional(readOnly = true)
    public AcudienteResponse obtenerPorCedula(String cedula) {
        try {
            Optional<Acudiente> acudiente = acudienteRepository.findByCedula(cedula);
            if (acudiente.isEmpty()) {
                return null;
            }
            return AcudienteEntityMapper.toResponse(acudiente.get());
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al consultar acudiente por cédula", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener acudiente: " + e.getMessage(), e);
        }
    }

    /**
     * Crea un nuevo acudiente
     * @param request Datos del acudiente a crear
     * @return AcudienteResponse del acudiente creado
     */
    @Transactional
    public AcudienteResponse crear(AcudienteRequest request) {
        try {
            Acudiente acudiente = AcudienteEntityMapper.toEntity(request);
            Acudiente acudienteGuardado = acudienteRepository.save(acudiente);

            AcudienteResponse response = AcudienteEntityMapper.toResponse(acudienteGuardado);
            response.setMessage("Acudiente creado exitosamente");
            return response;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al crear acudiente", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al crear acudiente: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza un acudiente existente
     * @param id ID del acudiente a actualizar
     * @param request Datos actualizados del acudiente
     * @return AcudienteResponse del acudiente actualizado, null si no existe
     */
    @Transactional
    public AcudienteResponse actualizar(Integer id, AcudienteRequest request) {
        try {
            Optional<Acudiente> acudienteExistente = acudienteRepository.findById(id);

            if (acudienteExistente.isEmpty()) {
                return null;
            }

            Acudiente acudiente = acudienteExistente.get();
            AcudienteEntityMapper.updateEntity(acudiente, request);
            Acudiente acudienteActualizado = acudienteRepository.save(acudiente);

            AcudienteResponse response = AcudienteEntityMapper.toResponse(acudienteActualizado);
            response.setMessage("Acudiente actualizado exitosamente");
            return response;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al actualizar acudiente: "+ e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al actualizar acudiente: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un acudiente por su ID
     * @param id ID del acudiente a eliminar
     * @return true si se eliminó, false si no existía
     */
    @Transactional
    public boolean eliminar(Integer id) {
        try {
            if (!acudienteRepository.existsById(id)) {
                return false;
            }
            acudienteRepository.deleteById(id);
            return true;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al eliminar acudiente", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al eliminar acudiente: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica si existe un acudiente con el ID dado
     * @param id ID del acudiente
     * @return true si existe, false si no
     */
    @Transactional(readOnly = true)
    public boolean existe(Integer id) {
        try {
            return acudienteRepository.existsById(id);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al verificar existencia de acudiente", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al verificar acudiente: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica si existe un acudiente con la cédula dada
     * @param cedula Cédula del acudiente
     * @return true si existe, false si no
     */
    @Transactional(readOnly = true)
    public boolean existePorCedula(String cedula) {
        try {
            return acudienteRepository.findByCedula(cedula).isPresent();
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al verificar existencia de acudiente por cédula", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al verificar acudiente: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene la entidad Acudiente por su ID (para uso interno de servicios)
     * @param id ID del acudiente
     * @return Optional<Acudiente>
     */
    @Transactional(readOnly = true)
    public Optional<Acudiente> obtenerEntidadPorId(Integer id) {
        try {
            return acudienteRepository.findById(id);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al obtener entidad acudiente", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener entidad: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene la entidad Acudiente por su cédula (para uso interno de servicios)
     * @param cedula Cédula del acudiente
     * @return Optional<Acudiente>
     */
    @Transactional(readOnly = true)
    public Optional<Acudiente> obtenerEntidadPorCedula(String cedula) {
        try {
            return acudienteRepository.findByCedula(cedula);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al obtener entidad acudiente por cédula", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener entidad: " + e.getMessage(), e);
        }
    }
}
