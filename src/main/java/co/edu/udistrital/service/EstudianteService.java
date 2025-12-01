package co.edu.udistrital.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.dto.EstudianteRequest;
import co.edu.udistrital.dto.EstudianteResponse;
import co.edu.udistrital.dto.mapper.EstudianteEntityMapper;
import co.edu.udistrital.exception.DatabaseException;
import co.edu.udistrital.model.Acudiente;
import co.edu.udistrital.model.Estudiante;
import co.edu.udistrital.model.Grupo;
import co.edu.udistrital.model.Estudiante.Estado;
import co.edu.udistrital.repository.EstudianteRepository;
import co.edu.udistrital.repository.GrupoRepository;

@Service
public class EstudianteService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private AcudienteService acudienteService;

    @Autowired
    private GrupoRepository grupoRepository;

    /**
     * Obtiene todos los estudiantes
     * 
     * @return Lista de EstudianteResponse
     */
    @Transactional(readOnly = true)
    public List<EstudianteResponse> obtenerTodos() {
        try {
            List<Estudiante> estudiantes = estudianteRepository.findAll();
            return EstudianteEntityMapper.toResponseList(estudiantes);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al consultar estudiantes", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener estudiantes: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene un estudiante por su ID
     * 
     * @param id ID del estudiante
     * @return EstudianteResponse si existe, null si no existe
     */
    @Transactional(readOnly = true)
    public EstudianteResponse obtenerPorId(Integer id) {
        try {
            Optional<Estudiante> estudiante = estudianteRepository.findById(id);
            if (estudiante.isEmpty()) {
                return null;
            }
            return EstudianteEntityMapper.toResponse(estudiante.get());
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al consultar estudiante por ID", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener estudiante: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene estudiantes por su estado
     * 
     * @param estado Estado del estudiante (aspirante, aceptado, rechazado)
     * @return Lista de EstudianteResponse
     */
    @Transactional(readOnly = true)
    public List<EstudianteResponse> obtenerPorEstado(Estado estado) {
        try {
            Optional<List<Estudiante>> estudiantes = estudianteRepository.findByEstado(estado);
            if (estudiantes.isEmpty() || estudiantes.get().isEmpty()) {
                return List.of();
            }
            return EstudianteEntityMapper.toResponseList(estudiantes.get());
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al consultar estudiantes por estado", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener estudiantes: " + e.getMessage(), e);
        }
    }

    /**
     * Crea un nuevo estudiante
     * 
     * @param request Datos del estudiante a crear
     * @return EstudianteResponse del estudiante creado
     */
    @Transactional
    public EstudianteResponse crear(EstudianteRequest request) {
        try {
            // Obtener el acudiente asociado
            Optional<Acudiente> acudienteOpt = acudienteService.obtenerEntidadPorId(request.getAcudiente_id());
            if (acudienteOpt.isEmpty()) {
                throw new RuntimeException("Acudiente no encontrado con ID: " + request.getAcudiente_id());
            }

            Acudiente acudiente = acudienteOpt.get();
            Estudiante estudiante = EstudianteEntityMapper.toEntity(request, acudiente);
            Estudiante estudianteGuardado = estudianteRepository.save(estudiante);

            EstudianteResponse response = EstudianteEntityMapper.toResponse(estudianteGuardado);
            response.setMessage("Estudiante creado exitosamente");
            return response;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al crear estudiante", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al crear estudiante: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza un estudiante existente
     * 
     * @param id      ID del estudiante a actualizar
     * @param request Datos actualizados del estudiante
     * @return EstudianteResponse del estudiante actualizado, null si no existe
     */
    @Transactional
    public EstudianteResponse actualizar(Integer id, EstudianteRequest request) {
        try {
            Optional<Estudiante> estudianteExistente = estudianteRepository.findById(id);

            if (estudianteExistente.isEmpty()) {
                return null;
            }

            Estudiante estudiante = estudianteExistente.get();
            EstudianteEntityMapper.updateEntity(estudiante, request);
            Optional<Grupo> grupoAasignar = grupoRepository.findById(id);

            if (request.getGrupo_id() != 0 && grupoAasignar.isPresent())
                estudiante.setGrupo(grupoAasignar.get());
            
            Estudiante estudianteActualizado = estudianteRepository.save(estudiante);

            EstudianteResponse response = EstudianteEntityMapper.toResponse(estudianteActualizado);
            response.setMessage("Estudiante actualizado exitosamente");
            return response;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al actualizar estudiante", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al actualizar estudiante: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza el estado de un estudiante
     * 
     * @param id          ID del estudiante
     * @param nuevoEstado Nuevo estado del estudiante
     * @return EstudianteResponse del estudiante actualizado, null si no existe
     */
    @Transactional
    public EstudianteResponse actualizarEstado(Integer id, Estado nuevoEstado) {
        try {
            Optional<Estudiante> estudianteExistente = estudianteRepository.findById(id);

            if (estudianteExistente.isEmpty()) {
                return null;
            }

            Estudiante estudiante = estudianteExistente.get();
            estudiante.setEstado(nuevoEstado);
            Estudiante estudianteActualizado = estudianteRepository.save(estudiante);

            EstudianteResponse response = EstudianteEntityMapper.toResponse(estudianteActualizado);
            response.setMessage("Estado del estudiante actualizado exitosamente");
            return response;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al actualizar estado del estudiante", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al actualizar estado: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un estudiante por su ID
     * 
     * @param id ID del estudiante a eliminar
     * @return true si se eliminó, false si no existía
     */
    @Transactional
    public boolean eliminar(Integer id) {
        try {
            if (!estudianteRepository.existsById(id)) {
                return false;
            }
            estudianteRepository.deleteById(id);
            return true;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al eliminar estudiante", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al eliminar estudiante: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica si existe un estudiante con el ID dado
     * 
     * @param id ID del estudiante
     * @return true si existe, false si no
     */
    @Transactional(readOnly = true)
    public boolean existe(Integer id) {
        try {
            return estudianteRepository.existsById(id);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al verificar existencia de estudiante", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al verificar estudiante: " + e.getMessage(), e);
        }
    }

    /**
     * Asigna un estudiante a un grupo
     * 
     * @param estudianteId ID del estudiante
     * @param grupo        Grupo a asignar (puede ser null para quitar el grupo)
     * @return EstudianteResponse del estudiante actualizado
     */
    @Transactional
    public EstudianteResponse asignarGrupo(Integer estudianteId, Grupo grupo) {
        try {
            Optional<Estudiante> estudianteOpt = estudianteRepository.findById(estudianteId);

            if (estudianteOpt.isEmpty()) {
                return null;
            }

            Estudiante estudiante = estudianteOpt.get();
            estudiante.setGrupo(grupo);
            Estudiante estudianteActualizado = estudianteRepository.save(estudiante);

            EstudianteResponse response = EstudianteEntityMapper.toResponse(estudianteActualizado);
            response.setMessage(
                    grupo != null ? "Estudiante asignado al grupo exitosamente" : "Estudiante removido del grupo");
            return response;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al asignar grupo al estudiante", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al asignar grupo: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene estudiantes sin grupo asignado
     * 
     * @return Lista de EstudianteResponse
     */
    @Transactional(readOnly = true)
    public List<EstudianteResponse> obtenerSinGrupo() {
        try {
            List<Estudiante> estudiantes = estudianteRepository.findAll();
            List<Estudiante> sinGrupo = estudiantes.stream()
                    .filter(e -> e.getGrupo() == null)
                    .toList();
            return EstudianteEntityMapper.toResponseList(sinGrupo);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al consultar estudiantes sin grupo", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener estudiantes: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene la entidad Estudiante por su ID (para uso interno de servicios)
     * 
     * @param id ID del estudiante
     * @return Optional<Estudiante>
     */
    @Transactional(readOnly = true)
    public Optional<Estudiante> obtenerEntidadPorId(Integer id) {
        try {
            return estudianteRepository.findById(id);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al obtener entidad estudiante", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener entidad: " + e.getMessage(), e);
        }
    }
}
