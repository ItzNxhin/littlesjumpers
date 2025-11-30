package co.edu.udistrital.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.dto.EstudianteResponse;
import co.edu.udistrital.dto.PreinscripcionRequest;
import co.edu.udistrital.dto.PreinscripcionResponse;
import co.edu.udistrital.model.Preinscripcion.EstadoEntrevista;
import co.edu.udistrital.service.EntrevistasService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private EntrevistasService entrevistasService;

    /**
     * Obtener todos los estudiantes aspirantes
     * GET /api/admin/aspirantes
     */
    @GetMapping("/entrevistas/aspirantes")
    public ResponseEntity<?> obtenerAspirantes() {
        try {
            List<EstudianteResponse> aspirantes = entrevistasService.estudiantesAspirantes();
            return ResponseEntity.ok(aspirantes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener aspirantes: " + e.getMessage());
        }
    }

    /**
     * Obtener todas las preinscripciones
     * GET /api/admin/preinscripciones
     */
    @GetMapping("/entrevistas/preinscripciones")
    public ResponseEntity<?> obtenerPreinscripciones() {
        try {
            List<PreinscripcionResponse> preinscripciones = entrevistasService.obtenerTodasPreinscripciones();
            return ResponseEntity.ok(preinscripciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener preinscripciones: " + e.getMessage());
        }
    }

    /**
     * Obtener preinscripciones por estado de entrevista
     * GET /api/admin/preinscripciones/estado/{estado}
     * Estados: pendiente, programada, realizada
     */
    @GetMapping("/preinscripciones/estado/{estado}")
    public ResponseEntity<?> obtenerPreinscripcionesPorEstado(@PathVariable String estado) {
        try {
            EstadoEntrevista estadoEnum = EstadoEntrevista.valueOf(estado.toLowerCase());
            List<PreinscripcionResponse> preinscripciones =
                    entrevistasService.obtenerPreinscripcionesPorEstado(estadoEnum);
            return ResponseEntity.ok(preinscripciones);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Estado inválido. Estados válidos: pendiente, programada, realizada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener preinscripciones: " + e.getMessage());
        }
    }

    /**
     * Crear preinscripción para un estudiante
     * POST /api/admin/preinscripciones
     * Body: { "estudiante_id": 1 }
     */
    @PostMapping("entrevistas/preinscripcion")
    public ResponseEntity<?> crearPreinscripcion(@Valid @RequestBody PreinscripcionRequest request) {
        try {
            if (request.getEstudiante_id() == null) {
                return ResponseEntity.badRequest().body("El ID del estudiante es obligatorio");
            }

            PreinscripcionResponse response = entrevistasService.crearPreinscripcion(request.getEstudiante_id());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear preinscripción: " + e.getMessage());
        }
    }

    /**
     * Programar entrevista
     * PUT /api/admin/preinscripciones/{id}/programar
     * Body: { "fecha_entrevista": "2024-12-15T10:00:00" }
     */
    @PutMapping("/preinscripciones/{id}/programar")
    public ResponseEntity<?> programarEntrevista(
            @PathVariable Integer id,
            @Valid @RequestBody PreinscripcionRequest request) {
        try {
            if (request.getFecha_entrevista() == null) {
                return ResponseEntity.badRequest().body("La fecha de entrevista es obligatoria");
            }

            PreinscripcionResponse response =
                    entrevistasService.programarEntrevista(id, request.getFecha_entrevista());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al programar entrevista: " + e.getMessage());
        }
    }

    /**
     * Marcar entrevista como realizada
     * PUT /api/admin/preinscripciones/{id}/realizada
     */
    @PutMapping("/preinscripciones/{id}/realizada")
    public ResponseEntity<?> marcarEntrevistaRealizada(@PathVariable Integer id) {
        try {
            PreinscripcionResponse response = entrevistasService.marcarEntrevistaRealizada(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al marcar entrevista como realizada: " + e.getMessage());
        }
    }

    /**
     * Aceptar estudiante aspirante
     * PUT /api/admin/estudiantes/{id}/aceptar
     */
    @PutMapping("/estudiantes/{id}/aceptar")
    public ResponseEntity<?> aceptarEstudiante(@PathVariable Integer id) {
        try {
            EstudianteResponse response = entrevistasService.aceptarEstudiante(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al aceptar estudiante: " + e.getMessage());
        }
    }

    /**
     * Rechazar estudiante aspirante
     * PUT /api/admin/estudiantes/{id}/rechazar
     */
    @PutMapping("/estudiantes/{id}/rechazar")
    public ResponseEntity<?> rechazarEstudiante(@PathVariable Integer id) {
        System.out.println("HOLLLLAAA");
        try {
            EstudianteResponse response = entrevistasService.rechazarEstudiante(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al rechazar estudiante: " + e.getMessage());
        }
    }
}
