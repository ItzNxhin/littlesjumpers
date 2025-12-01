package co.edu.udistrital.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.dto.GrupoRequest;
import co.edu.udistrital.dto.GrupoResponse;
import co.edu.udistrital.model.Grupo.Grado;
import co.edu.udistrital.service.GrupoService;

@RestController
@RequestMapping("/api/gestion/grupos")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    /**
     * Obtiene todos los grupos
     * GET /api/gestion/grupos
     */
    @GetMapping
    public ResponseEntity<List<GrupoResponse>> listarTodos() {
        try {
            List<GrupoResponse> grupos = grupoService.obtenerTodos();
            return ResponseEntity.ok(grupos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene un grupo por ID
     * GET /api/gestion/grupos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<GrupoResponse> obtenerPorId(@PathVariable Integer id) {
        try {
            GrupoResponse grupo = grupoService.obtenerPorId(id);
            if (grupo == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(grupo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene grupos por grado
     * GET /api/gestion/grupos/grado/{grado}
     */
    @GetMapping("/grado/{grado}")
    public ResponseEntity<List<GrupoResponse>> obtenerPorGrado(@PathVariable Grado grado) {
        try {
            List<GrupoResponse> grupos = grupoService.obtenerPorGrado(grado);
            return ResponseEntity.ok(grupos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crea un nuevo grupo
     * POST /api/gestion/grupos
     */
    @PostMapping
    public ResponseEntity<GrupoResponse> crear(@RequestBody GrupoRequest request) {
        try {
            GrupoResponse grupo = grupoService.crear(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(grupo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Actualiza un grupo existente
     * PUT /api/gestion/grupos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<GrupoResponse> actualizar(
            @PathVariable Integer id,
            @RequestBody GrupoRequest request) {
        try {
            GrupoResponse grupo = grupoService.actualizar(id, request);
            if (grupo == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(grupo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Elimina un grupo
     * DELETE /api/gestion/grupos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            boolean eliminado = grupoService.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
