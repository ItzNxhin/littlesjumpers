package co.edu.udistrital.service;

import co.edu.udistrital.dto.CalificacionRequest;
import co.edu.udistrital.dto.CalificacionResponse;
import co.edu.udistrital.model.Calificacion;
import co.edu.udistrital.model.Estudiante;
import co.edu.udistrital.model.Logro;
import co.edu.udistrital.model.Profesor;
import co.edu.udistrital.repository.CalificacionesRepository;
import co.edu.udistrital.repository.EstudianteRepository;
import co.edu.udistrital.repository.LogroRepository;
import co.edu.udistrital.repository.ProfesorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CalificacionesService {

    @Autowired
    private CalificacionesRepository calificacionesRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private LogroRepository logroRepository;

    @Autowired
    private ProfesorRepository profesorRepository;

    /**
     * Obtiene todas las calificaciones
     */
    @Transactional(readOnly = true)
    public List<CalificacionResponse> obtenerTodas() {
        try {
            List<Calificacion> calificaciones = calificacionesRepository.findAll();
            return calificaciones.stream()
                    .map(CalificacionResponse::new)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al consultar calificaciones", e);
        }
    }

    /**
     * Obtiene una calificación por ID
     */
    @Transactional(readOnly = true)
    public CalificacionResponse obtenerPorId(Integer id) {
        try {
            Optional<Calificacion> calificacion = calificacionesRepository.findById(id);
            return calificacion.map(CalificacionResponse::new).orElse(null);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al consultar calificación", e);
        }
    }

    /**
     * Obtiene todas las calificaciones de un estudiante
     */
    @Transactional(readOnly = true)
    public List<CalificacionResponse> obtenerPorEstudiante(Integer estudianteId) {
        try {
            Optional<Estudiante> estudiante = estudianteRepository.findById(estudianteId);
            if (estudiante.isEmpty()) {
                throw new RuntimeException("Estudiante no encontrado");
            }

            List<Calificacion> calificaciones = calificacionesRepository.findByEstudiante(estudiante.get());
            return calificaciones.stream()
                    .map(CalificacionResponse::new)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al consultar calificaciones del estudiante", e);
        }
    }

    /**
     * Crea una nueva calificación
     */
    @Transactional
    public CalificacionResponse crear(CalificacionRequest request) {
        try {
            // Validar que el estudiante existe
            Estudiante estudiante = estudianteRepository.findById(request.getEstudianteId())
                    .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

            // Validar que el logro existe
            Logro logro = logroRepository.findById(request.getLogroId())
                    .orElseThrow(() -> new RuntimeException("Logro no encontrado"));

            // Validar que el profesor existe
            Profesor profesor = profesorRepository.findById(request.getProfesorId())
                    .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

            // Validar el valor de la calificación (debe estar entre 0 y 5)
            if (request.getValor().doubleValue() < 0 || request.getValor().doubleValue() > 5) {
                throw new RuntimeException("El valor de la calificación debe estar entre 0 y 5");
            }

            // Crear la calificación
            Calificacion calificacion = new Calificacion(
                    estudiante,
                    logro,
                    profesor,
                    request.getPeriodo(),
                    request.getValor()
            );

            Calificacion guardada = calificacionesRepository.save(calificacion);
            return new CalificacionResponse(guardada);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al crear calificación", e);
        }
    }

    /**
     * Actualiza una calificación existente
     */
    @Transactional
    public CalificacionResponse actualizar(Integer id, CalificacionRequest request) {
        try {
            Optional<Calificacion> calificacionExistente = calificacionesRepository.findById(id);

            if (calificacionExistente.isEmpty()) {
                return null;
            }

            Calificacion calificacion = calificacionExistente.get();

            // Validar el valor de la calificación (debe estar entre 0 y 5)
            if (request.getValor().doubleValue() < 0 || request.getValor().doubleValue() > 5) {
                throw new RuntimeException("El valor de la calificación debe estar entre 0 y 5");
            }

            // Actualizar solo el valor y el periodo (no permitimos cambiar estudiante, logro o profesor)
            calificacion.setValor(request.getValor());
            calificacion.setPeriodo(request.getPeriodo());

            Calificacion actualizada = calificacionesRepository.save(calificacion);
            return new CalificacionResponse(actualizada);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al actualizar calificación", e);
        }
    }

    /**
     * Elimina una calificación
     */
    @Transactional
    public boolean eliminar(Integer id) {
        try {
            if (!calificacionesRepository.existsById(id)) {
                return false;
            }
            calificacionesRepository.deleteById(id);
            return true;
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al eliminar calificación", e);
        }
    }
}
