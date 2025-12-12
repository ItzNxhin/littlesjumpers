package co.edu.udistrital.controller;

import co.edu.udistrital.dto.CalificacionResponse;
import co.edu.udistrital.dto.EstudianteResponse;
import co.edu.udistrital.dto.ObservacionResponse;
import co.edu.udistrital.service.AcademicoAcudienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/acudiente")
@CrossOrigin(origins = "*")
public class AcudienteSesionController {

    @Autowired
    private AcademicoAcudienteService academicoAcudienteService;

    /**
     * Obtiene los hijos (estudiantes) del acudiente
     * GET /api/acudiente/{acudienteId}/hijos
     */
    @GetMapping("/{acudienteId}/hijos")
    public ResponseEntity<?> obtenerHijos(@PathVariable Integer acudienteId) {
        try {
            List<EstudianteResponse> hijos = academicoAcudienteService.obtenerHijosDelAcudiente(acudienteId);
            return ResponseEntity.ok(hijos);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al obtener hijos del acudiente");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Obtiene todas las observaciones de un estudiante (hijo)
     * GET /api/acudiente/{acudienteId}/estudiantes/{estudianteId}/observaciones
     */
    @GetMapping("/{acudienteId}/estudiantes/{estudianteId}/observaciones")
    public ResponseEntity<?> obtenerObservacionesEstudiante(
            @PathVariable Integer acudienteId,
            @PathVariable Integer estudianteId) {
        try {
            List<ObservacionResponse> observaciones = academicoAcudienteService.obtenerObservacionesEstudiante(acudienteId, estudianteId);
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
     * Obtiene todas las calificaciones de un estudiante (hijo)
     * GET /api/acudiente/{acudienteId}/estudiantes/{estudianteId}/calificaciones
     */
    @GetMapping("/{acudienteId}/estudiantes/{estudianteId}/calificaciones")
    public ResponseEntity<?> obtenerCalificacionesEstudiante(
            @PathVariable Integer acudienteId,
            @PathVariable Integer estudianteId) {
        try {
            List<CalificacionResponse> calificaciones = academicoAcudienteService.obtenerCalificacionesEstudiante(acudienteId, estudianteId);
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
}
