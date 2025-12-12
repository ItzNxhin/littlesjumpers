package co.edu.udistrital.controller;

import co.edu.udistrital.dto.CalificacionRequest;
import co.edu.udistrital.dto.CalificacionResponse;
import co.edu.udistrital.dto.EstudianteResponse;
import co.edu.udistrital.dto.HojaVidaResponse;
import co.edu.udistrital.dto.ObservacionRequest;
import co.edu.udistrital.dto.ObservacionResponse;
import co.edu.udistrital.service.AcademicoProfesorService;
import co.edu.udistrital.service.HojaVidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profesor")
public class AcademicoProfesorController {

    @Autowired
    private AcademicoProfesorService academicoProfesorService;

    @Autowired
    private HojaVidaService hojaVidaService;

    /**
     * Obtiene los estudiantes del grupo asignado al profesor
     * GET /api/profesor/{profesorId}/estudiantes
     */
    @GetMapping("/{profesorId}/estudiantes")
    public ResponseEntity<?> obtenerEstudiantesDelGrupo(@PathVariable Integer profesorId) {
        try {
            List<EstudianteResponse> estudiantes = academicoProfesorService.obtenerEstudiantesDelGrupoProfesor(profesorId);
            return ResponseEntity.ok(estudiantes);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al obtener estudiantes");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Califica a un estudiante
     * POST /api/profesor/{profesorId}/calificar
     */
    @PostMapping("/{profesorId}/calificar")
    public ResponseEntity<?> calificarEstudiante(
            @PathVariable Integer profesorId,
            @RequestBody CalificacionRequest request) {
        try {
            CalificacionResponse calificacion = academicoProfesorService.calificarEstudiante(profesorId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(calificacion);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al calificar estudiante");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Obtiene todas las calificaciones de un estudiante
     * GET /api/profesor/estudiantes/{estudianteId}/calificaciones
     */
    @GetMapping("/estudiantes/{estudianteId}/calificaciones")
    public ResponseEntity<?> obtenerCalificacionesEstudiante(@PathVariable Integer estudianteId) {
        try {
            List<CalificacionResponse> calificaciones = academicoProfesorService.obtenerCalificacionesEstudiante(estudianteId);
            return ResponseEntity.ok(calificaciones);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al obtener calificaciones");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Obtiene todas las calificaciones de los estudiantes del grupo del profesor
     * GET /api/profesor/{profesorId}/calificaciones
     */
    @GetMapping("/{profesorId}/calificaciones")
    public ResponseEntity<?> obtenerCalificacionesDelGrupo(@PathVariable Integer profesorId) {
        try {
            List<CalificacionResponse> calificaciones = academicoProfesorService.obtenerCalificacionesDelGrupo(profesorId);
            return ResponseEntity.ok(calificaciones);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al obtener calificaciones del grupo");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Crea una nueva observación sobre el comportamiento de un estudiante
     * POST /api/profesor/{profesorId}/observacion
     */
    @PostMapping("/{profesorId}/observacion")
    public ResponseEntity<?> crearObservacion(
            @PathVariable Integer profesorId,
            @RequestBody ObservacionRequest request) {
        try {
            ObservacionResponse observacion = academicoProfesorService.crearObservacion(profesorId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(observacion);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al crear observación");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Obtiene todas las observaciones de un estudiante
     * GET /api/profesor/estudiantes/{estudianteId}/observaciones
     */
    @GetMapping("/estudiantes/{estudianteId}/observaciones")
    public ResponseEntity<?> obtenerObservacionesEstudiante(@PathVariable Integer estudianteId) {
        try {
            List<ObservacionResponse> observaciones = academicoProfesorService.obtenerObservacionesEstudiante(estudianteId);
            return ResponseEntity.ok(observaciones);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al obtener observaciones");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Actualiza una observación existente
     * PUT /api/profesor/{profesorId}/observacion/{observacionId}
     */
    @PutMapping("/{profesorId}/observacion/{observacionId}")
    public ResponseEntity<?> actualizarObservacion(
            @PathVariable Integer profesorId,
            @PathVariable Integer observacionId,
            @RequestBody ObservacionRequest request) {
        try {
            ObservacionResponse observacion = academicoProfesorService.actualizarObservacion(profesorId, observacionId, request);
            return ResponseEntity.ok(observacion);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al actualizar observación");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Elimina una observación
     * DELETE /api/profesor/{profesorId}/observacion/{observacionId}
     */
    @DeleteMapping("/{profesorId}/observacion/{observacionId}")
    public ResponseEntity<?> eliminarObservacion(
            @PathVariable Integer profesorId,
            @PathVariable Integer observacionId) {
        try {
            academicoProfesorService.eliminarObservacion(profesorId, observacionId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Observación eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al eliminar observación");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Obtiene la hoja de vida de un estudiante
     * GET /api/profesor/estudiantes/{estudianteId}/hoja-vida
     */
    @GetMapping("/estudiantes/{estudianteId}/hoja-vida")
    public ResponseEntity<?> obtenerHojaVidaEstudiante(@PathVariable Integer estudianteId) {
        try {
            HojaVidaResponse hojaVida = hojaVidaService.obtenerOCrearPorEstudiante(estudianteId);
            return ResponseEntity.ok(hojaVida);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al obtener hoja de vida");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
