package co.edu.udistrital.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.dto.CalificacionResponse;
import co.edu.udistrital.dto.EstudianteResponse;
import co.edu.udistrital.dto.ObservacionResponse;
import co.edu.udistrital.model.Acudiente;
import co.edu.udistrital.model.Observador;
import co.edu.udistrital.repository.ObservadorRepository;

@Service
public class AcademicoAcudienteService {

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private AcudienteService acudienteService;

    @Autowired
    private ObservadorRepository observadorRepository;

    @Autowired
    private CalificacionesService calificacionesService;

    /**
     * Obtiene los hijos (estudiantes) de un acudiente
     *
     * @param acudienteId ID del acudiente
     * @return Lista de estudiantes (hijos) del acudiente
     */
    @Transactional(readOnly = true)
    public List<EstudianteResponse> obtenerHijosDelAcudiente(Integer acudienteId) {
        try {
            // Validar que el acudiente existe
            Optional<Acudiente> acudienteOpt = acudienteService.obtenerEntidadPorId(acudienteId);
            if (acudienteOpt.isEmpty()) {
                throw new RuntimeException("Acudiente no encontrado");
            }

            // Obtener los estudiantes del acudiente
            return estudianteService.obtenerPorAcudiente(acudienteId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener hijos del acudiente: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todas las observaciones de un estudiante
     * (El acudiente puede ver las observaciones de sus hijos)
     *
     * @param acudienteId ID del acudiente
     * @param estudianteId ID del estudiante
     * @return Lista de observaciones del estudiante
     */
    @Transactional(readOnly = true)
    public List<ObservacionResponse> obtenerObservacionesEstudiante(Integer acudienteId, Integer estudianteId) {
        try {
            // Validar que el estudiante pertenece al acudiente
            List<EstudianteResponse> hijos = obtenerHijosDelAcudiente(acudienteId);
            boolean esHijo = hijos.stream()
                    .anyMatch(e -> e.getId() == estudianteId);

            if (!esHijo) {
                throw new RuntimeException("El estudiante no pertenece a este acudiente");
            }

            // Obtener las observaciones
            List<Observador> observaciones = observadorRepository.findByEstudianteIdOrderByFechaDesc(estudianteId);
            return observaciones.stream()
                    .map(ObservacionResponse::new)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener observaciones del estudiante: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todas las calificaciones de un estudiante (hijo)
     * (El acudiente puede ver las calificaciones de sus hijos)
     *
     * @param acudienteId ID del acudiente
     * @param estudianteId ID del estudiante
     * @return Lista de calificaciones del estudiante
     */
    @Transactional(readOnly = true)
    public List<CalificacionResponse> obtenerCalificacionesEstudiante(Integer acudienteId, Integer estudianteId) {
        try {
            // Validar que el estudiante pertenece al acudiente
            List<EstudianteResponse> hijos = obtenerHijosDelAcudiente(acudienteId);
            boolean esHijo = hijos.stream()
                    .anyMatch(e -> e.getId() == estudianteId);

            if (!esHijo) {
                throw new RuntimeException("El estudiante no pertenece a este acudiente");
            }

            // Obtener las calificaciones
            return calificacionesService.obtenerPorEstudiante(estudianteId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener calificaciones del estudiante: " + e.getMessage(), e);
        }
    }
}
