package co.edu.udistrital.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.dto.GrupoRequest;
import co.edu.udistrital.dto.GrupoResponse;
import co.edu.udistrital.dto.mapper.GrupoEntityMapper;
import co.edu.udistrital.exception.DatabaseException;
import co.edu.udistrital.model.Grupo;
import co.edu.udistrital.model.Grupo.Grado;
import co.edu.udistrital.model.Profesor;
import co.edu.udistrital.repository.GrupoRepository;

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private ProfesorService profesorService;

    /**
     * Obtiene todos los grupos
     */
    @Transactional(readOnly = true)
    public List<GrupoResponse> obtenerTodos() {
        try {
            List<Grupo> grupos = grupoRepository.findAll();
            List<GrupoResponse> responses = GrupoEntityMapper.toResponseList(grupos);

            // Agregar conteo de estudiantes a cada grupo
            for (int i = 0; i < grupos.size(); i++) {
                Long count = grupoRepository.contarEstudiantesPorGrupo(grupos.get(i).getId());
                responses.get(i).setEstudiantes_count(count.intValue());
            }

            return responses;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al consultar grupos", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener grupos: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene un grupo por su ID
     */
    @Transactional(readOnly = true)
    public GrupoResponse obtenerPorId(Integer id) {
        try {
            Optional<Grupo> grupo = grupoRepository.findById(id);
            if (grupo.isEmpty()) {
                return null;
            }

            GrupoResponse response = GrupoEntityMapper.toResponse(grupo.get());
            Long count = grupoRepository.contarEstudiantesPorGrupo(id);
            response.setEstudiantes_count(count.intValue());

            return response;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al consultar grupo por ID", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener grupo: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene grupos por grado
     */
    @Transactional(readOnly = true)
    public List<GrupoResponse> obtenerPorGrado(Grado grado) {
        try {
            List<Grupo> grupos = grupoRepository.findByGrado(grado);
            return GrupoEntityMapper.toResponseList(grupos);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al consultar grupos por grado", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener grupos: " + e.getMessage(), e);
        }
    }

    /**
     * Crea un nuevo grupo
     */
    @Transactional
    public GrupoResponse crear(GrupoRequest request) {
        try {
            Profesor profesor = null;

            // Si se especifica un profesor, verificar que existe
            if (request.getProfesor_id() != null) {
                Optional<Profesor> profesorOpt = profesorService.obtenerEntidadPorId(request.getProfesor_id());
                if (profesorOpt.isEmpty()) {
                    throw new RuntimeException("Profesor no encontrado con ID: " + request.getProfesor_id());
                }
                profesor = profesorOpt.get();
            }

            Grupo grupo = GrupoEntityMapper.toEntity(request, profesor);
            Grupo grupoGuardado = grupoRepository.save(grupo);

            GrupoResponse response = GrupoEntityMapper.toResponse(grupoGuardado);
            response.setEstudiantes_count(0);
            response.setMessage("Grupo creado exitosamente");
            return response;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al crear grupo", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al crear grupo: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza un grupo existente
     */
    @Transactional
    public GrupoResponse actualizar(Integer id, GrupoRequest request) {
        try {
            Optional<Grupo> grupoExistente = grupoRepository.findById(id);

            if (grupoExistente.isEmpty()) {
                return null;
            }

            Profesor profesor = null;
            if (request.getProfesor_id() != null) {
                Optional<Profesor> profesorOpt = profesorService.obtenerEntidadPorId(request.getProfesor_id());
                if (profesorOpt.isEmpty()) {
                    throw new RuntimeException("Profesor no encontrado con ID: " + request.getProfesor_id());
                }
                profesor = profesorOpt.get();
            }

            Grupo grupo = grupoExistente.get();
            GrupoEntityMapper.updateEntity(grupo, request, profesor);
            Grupo grupoActualizado = grupoRepository.save(grupo);

            GrupoResponse response = GrupoEntityMapper.toResponse(grupoActualizado);
            response.setMessage("Grupo actualizado exitosamente");
            return response;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al actualizar grupo", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al actualizar grupo: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un grupo por su ID
     */
    @Transactional
    public boolean eliminar(Integer id) {
        try {
            if (!grupoRepository.existsById(id)) {
                return false;
            }

            // Verificar que no tenga estudiantes asignados
            Long count = grupoRepository.contarEstudiantesPorGrupo(id);
            if (count > 0) {
                throw new RuntimeException("No se puede eliminar un grupo con estudiantes asignados");
            }

            grupoRepository.deleteById(id);
            return true;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al eliminar grupo", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al eliminar grupo: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene la entidad Grupo por su ID (para uso interno de servicios)
     */
    @Transactional(readOnly = true)
    public Optional<Grupo> obtenerEntidadPorId(Integer id) {
        try {
            return grupoRepository.findById(id);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al obtener entidad grupo", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener entidad: " + e.getMessage(), e);
        }
    }
}
