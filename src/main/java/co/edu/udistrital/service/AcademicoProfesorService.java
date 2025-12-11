package co.edu.udistrital.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.dto.CalificacionRequest;
import co.edu.udistrital.dto.CalificacionResponse;
import co.edu.udistrital.dto.EstudianteResponse;
import co.edu.udistrital.dto.ObservacionRequest;
import co.edu.udistrital.dto.ObservacionResponse;
import co.edu.udistrital.model.Estudiante;
import co.edu.udistrital.model.Grupo;
import co.edu.udistrital.model.Observador;
import co.edu.udistrital.model.Profesor;
import co.edu.udistrital.repository.ObservadorRepository;

@Service
public class AcademicoProfesorService {

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private CalificacionesService calificacionesService;

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private ProfesorService profesorService;

    @Autowired
    private ObservadorRepository observadorRepository;

    /**
     * Obtiene los estudiantes del grupo asignado a un profesor
     *
     * @param profesorId ID del profesor
     * @return Lista de estudiantes del grupo del profesor
     */
    @Transactional(readOnly = true)
    public List<EstudianteResponse> obtenerEstudiantesDelGrupoProfesor(Integer profesorId) {
        try {
            // Obtener el profesor
            Optional<Profesor> profesorOpt = profesorService.obtenerEntidadPorId(profesorId);
            if (profesorOpt.isEmpty()) {
                throw new RuntimeException("Profesor no encontrado");
            }

            // Obtener el grupo del profesor
            Grupo grupo = grupoService.obtenerGrupoPorProfesor(profesorId);
            if (grupo == null) {
                throw new RuntimeException("El profesor no tiene un grupo asignado");
            }

            // Obtener los estudiantes del grupo
            return estudianteService.obtenerPorGrupo(grupo.getId());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener estudiantes del grupo: " + e.getMessage(), e);
        }
    }

    /**
     * Califica a un estudiante en un logro específico
     *
     * @param profesorId ID del profesor que califica
     * @param request Datos de la calificación
     * @return CalificacionResponse con los datos de la calificación creada
     */
    @Transactional
    public CalificacionResponse calificarEstudiante(Integer profesorId, CalificacionRequest request) {
        try {
            // Validar que el profesor existe
            Optional<Profesor> profesorOpt = profesorService.obtenerEntidadPorId(profesorId);
            if (profesorOpt.isEmpty()) {
                throw new RuntimeException("Profesor no encontrado");
            }

            // Validar que el estudiante pertenece al grupo del profesor
            List<EstudianteResponse> estudiantesDelGrupo = obtenerEstudiantesDelGrupoProfesor(profesorId);
            boolean estudianteEnGrupo = estudiantesDelGrupo.stream()
                    .anyMatch(e -> e.getId() == request.getEstudianteId());

            if (!estudianteEnGrupo) {
                throw new RuntimeException("El estudiante no pertenece al grupo del profesor");
            }

            // Asignar el ID del profesor al request
            request.setProfesorId(profesorId);

            // Crear la calificación
            return calificacionesService.crear(request);
        } catch (Exception e) {
            throw new RuntimeException("Error al calificar estudiante: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todas las calificaciones de un estudiante
     *
     * @param estudianteId ID del estudiante
     * @return Lista de calificaciones del estudiante
     */
    @Transactional(readOnly = true)
    public List<CalificacionResponse> obtenerCalificacionesEstudiante(Integer estudianteId) {
        try {
            return calificacionesService.obtenerPorEstudiante(estudianteId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener calificaciones del estudiante: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todas las calificaciones de los estudiantes del grupo del profesor
     *
     * @param profesorId ID del profesor
     * @return Lista de todas las calificaciones de los estudiantes del grupo
     */
    @Transactional(readOnly = true)
    public List<CalificacionResponse> obtenerCalificacionesDelGrupo(Integer profesorId) {
        try {
            List<EstudianteResponse> estudiantes = obtenerEstudiantesDelGrupoProfesor(profesorId);

            return estudiantes.stream()
                    .flatMap(estudiante -> calificacionesService.obtenerPorEstudiante(estudiante.getId()).stream())
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener calificaciones del grupo: " + e.getMessage(), e);
        }
    }

    /**
     * Crea una nueva observación sobre el comportamiento de un estudiante
     *
     * @param profesorId ID del profesor que registra la observación
     * @param request Datos de la observación
     * @return ObservacionResponse con los datos de la observación creada
     */
    @Transactional
    public ObservacionResponse crearObservacion(Integer profesorId, ObservacionRequest request) {
        try {
            // Validar que el profesor existe
            Profesor profesor = profesorService.obtenerEntidadPorId(profesorId)
                    .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

            // Validar que el estudiante pertenece al grupo del profesor
            List<EstudianteResponse> estudiantesDelGrupo = obtenerEstudiantesDelGrupoProfesor(profesorId);
            boolean estudianteEnGrupo = estudiantesDelGrupo.stream()
                    .anyMatch(e -> e.getId() == request.getEstudianteId());

            if (!estudianteEnGrupo) {
                throw new RuntimeException("El estudiante no pertenece al grupo del profesor");
            }

            // Obtener el estudiante
            Estudiante estudiante = estudianteService.obtenerEntidadPorId(request.getEstudianteId())
                    .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

            // Crear la observación
            Observador observacion = new Observador(estudiante, profesor, request.getTexto());
            Observador guardada = observadorRepository.save(observacion);

            return new ObservacionResponse(guardada);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear observación: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todas las observaciones de un estudiante
     *
     * @param estudianteId ID del estudiante
     * @return Lista de observaciones del estudiante ordenadas por fecha descendente
     */
    @Transactional(readOnly = true)
    public List<ObservacionResponse> obtenerObservacionesEstudiante(Integer estudianteId) {
        try {
            List<Observador> observaciones = observadorRepository.findByEstudianteIdOrderByFechaDesc(estudianteId);
            return observaciones.stream()
                    .map(ObservacionResponse::new)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener observaciones del estudiante: " + e.getMessage(), e);
        }
    }
}
