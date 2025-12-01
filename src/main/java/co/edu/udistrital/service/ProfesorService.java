package co.edu.udistrital.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.dto.ProfesorRequest;
import co.edu.udistrital.dto.ProfesorResponse;
import co.edu.udistrital.dto.mapper.ProfesorEntityMapper;
import co.edu.udistrital.exception.DatabaseException;
import co.edu.udistrital.model.Profesor;
import co.edu.udistrital.repository.ProfesorRepository;

@Service
public class ProfesorService {

    @Autowired
    private ProfesorRepository profesorRepository;

    /**
     * Obtiene todos los profesores
     * @return Lista de ProfesorResponse
     */
    @Transactional(readOnly = true)
    public List<ProfesorResponse> obtenerTodos() {
        try {
            List<Profesor> profesores = profesorRepository.findAll();
            return ProfesorEntityMapper.toResponseList(profesores);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al consultar profesores", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener profesores: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene un profesor por su ID
     * @param id ID del profesor
     * @return ProfesorResponse si existe, null si no existe
     */
    @Transactional(readOnly = true)
    public ProfesorResponse obtenerPorId(Integer id) {
        try {
            Optional<Profesor> profesor = profesorRepository.findById(id);
            if (profesor.isEmpty()) {
                return null;
            }
            return ProfesorEntityMapper.toResponse(profesor.get());
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al consultar profesor por ID", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener profesor: " + e.getMessage(), e);
        }
    }

    /**
     * Crea un nuevo profesor
     * @param request Datos del profesor a crear
     * @return ProfesorResponse del profesor creado
     */
    @Transactional
    public ProfesorResponse crear(ProfesorRequest request) {
        try {
            Profesor profesor = ProfesorEntityMapper.toEntity(request);
            Profesor profesorGuardado = profesorRepository.save(profesor);

            ProfesorResponse response = ProfesorEntityMapper.toResponse(profesorGuardado);
            response.setMessage("Profesor creado exitosamente");
            return response;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al crear profesor", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al crear profesor: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza un profesor existente
     * @param id ID del profesor a actualizar
     * @param request Datos actualizados del profesor
     * @return ProfesorResponse del profesor actualizado, null si no existe
     */
    @Transactional
    public ProfesorResponse actualizar(Integer id, ProfesorRequest request) {
        try {
            Optional<Profesor> profesorExistente = profesorRepository.findById(id);

            if (profesorExistente.isEmpty()) {
                return null;
            }

            Profesor profesor = profesorExistente.get();
            ProfesorEntityMapper.updateEntity(profesor, request);
            Profesor profesorActualizado = profesorRepository.save(profesor);

            ProfesorResponse response = ProfesorEntityMapper.toResponse(profesorActualizado);
            response.setMessage("Profesor actualizado exitosamente");
            return response;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al actualizar profesor", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al actualizar profesor: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un profesor por su ID
     * @param id ID del profesor a eliminar
     * @return true si se eliminó, false si no existía
     */
    @Transactional
    public boolean eliminar(Integer id) {
        try {
            if (!profesorRepository.existsById(id)) {
                return false;
            }
            profesorRepository.deleteById(id);
            return true;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al eliminar profesor", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al eliminar profesor: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica si existe un profesor con el ID dado
     * @param id ID del profesor
     * @return true si existe, false si no
     */
    @Transactional(readOnly = true)
    public boolean existe(Integer id) {
        try {
            return profesorRepository.existsById(id);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al verificar existencia de profesor", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al verificar profesor: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene la entidad Profesor por su ID (para uso interno de servicios)
     * @param id ID del profesor
     * @return Optional<Profesor>
     */
    @Transactional(readOnly = true)
    public Optional<Profesor> obtenerEntidadPorId(Integer id) {
        try {
            return profesorRepository.findById(id);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al obtener entidad profesor", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener entidad: " + e.getMessage(), e);
        }
    }
}
