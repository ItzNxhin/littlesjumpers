package co.edu.udistrital.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.dto.EstudianteRequest;
import co.edu.udistrital.dto.EstudianteResponse;
import co.edu.udistrital.model.Estudiante.Estado;
import co.edu.udistrital.model.Grupo;
import co.edu.udistrital.service.EstudianteService;
import co.edu.udistrital.service.GrupoService;

@RestController
@RequestMapping("/api/gestion/estudiantes")
public class GestionEstudiantesController {

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private GrupoService grupoService;

    /**
     * Obtiene todos los estudiantes
     * GET /api/gestion/estudiantes
     */
    @GetMapping
    public ResponseEntity<List<EstudianteResponse>> listarTodos() {
        try {
            List<EstudianteResponse> estudiantes = estudianteService.obtenerTodos();
            return ResponseEntity.ok(estudiantes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene estudiantes sin grupo
     * GET /api/gestion/estudiantes/sin-grupo
     */
    @GetMapping("/sin-grupo")
    public ResponseEntity<List<EstudianteResponse>> listarSinGrupo() {
        try {
            List<EstudianteResponse> estudiantes = estudianteService.obtenerSinGrupo();
            return ResponseEntity.ok(estudiantes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene estudiantes por grupo
     * GET /api/gestion/estudiantes/grupo/{grupoId}
     */
    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<List<EstudianteResponse>> listarPorGrupo(@PathVariable Integer grupoId) {
        try {
            List<EstudianteResponse> estudiantes = estudianteService.obtenerPorGrupo(grupoId);
            return ResponseEntity.ok(estudiantes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene estudiantes por estado
     * GET /api/gestion/estudiantes/estado/{estado}
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<EstudianteResponse>> listarPorEstado(@PathVariable Estado estado) {
        try {
            List<EstudianteResponse> estudiantes = estudianteService.obtenerPorEstado(estado);
            return ResponseEntity.ok(estudiantes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene un estudiante por ID
     * GET /api/gestion/estudiantes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<EstudianteResponse> obtenerPorId(@PathVariable Integer id) {
        try {
            EstudianteResponse estudiante = estudianteService.obtenerPorId(id);
            if (estudiante == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(estudiante);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crea un nuevo estudiante
     * POST /api/gestion/estudiantes
     */
    @PostMapping
    public ResponseEntity<EstudianteResponse> crear(@RequestBody EstudianteRequest request) {
        try {
            EstudianteResponse estudiante = estudianteService.crear(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(estudiante);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Actualiza un estudiante existente
     * PUT /api/gestion/estudiantes/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<EstudianteResponse> actualizar(
            @PathVariable Integer id,
            @RequestBody EstudianteRequest request) {
        try {
            EstudianteResponse estudiante = estudianteService.actualizar(id, request);

            if (estudiante == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(estudiante);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Actualiza el estado de un estudiante
     * PATCH /api/gestion/estudiantes/{id}/estado
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<EstudianteResponse> actualizarEstado(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        try {
            Estado nuevoEstado = Estado.valueOf(body.get("estado"));
            EstudianteResponse estudiante = estudianteService.actualizarEstado(id, nuevoEstado);
            if (estudiante == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(estudiante);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Asigna un estudiante a un grupo
     * PUT /api/gestion/estudiantes/{id}/grupo
     */
    @PutMapping("/{id}/grupo")
    public ResponseEntity<EstudianteResponse> asignarGrupo(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer grupoId) {
        try {
            Grupo grupo = null;
            if (grupoId != null) {
                grupo = grupoService.obtenerEntidadPorId(grupoId).orElse(null);
                if (grupo == null) {
                    return ResponseEntity.badRequest().build();
                }
            }

            EstudianteResponse estudiante = estudianteService.asignarGrupo(id, grupo);
            if (estudiante == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(estudiante);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Elimina un estudiante
     * DELETE /api/gestion/estudiantes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            boolean eliminado = estudianteService.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
