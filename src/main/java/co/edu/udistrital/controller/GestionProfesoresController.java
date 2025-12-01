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

import co.edu.udistrital.dto.ProfesorRequest;
import co.edu.udistrital.dto.ProfesorResponse;
import co.edu.udistrital.service.ProfesorService;

@RestController
@RequestMapping("/api/gestion/profesores")
public class GestionProfesoresController {

    @Autowired
    private ProfesorService profesorService;

    /**
     * Obtiene todos los profesores
     * GET /api/gestion/profesores
     */
    @GetMapping
    public ResponseEntity<List<ProfesorResponse>> listarTodos() {
        try {
            List<ProfesorResponse> profesores = profesorService.obtenerTodos();
            return ResponseEntity.ok(profesores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene un profesor por ID
     * GET /api/gestion/profesores/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProfesorResponse> obtenerPorId(@PathVariable Integer id) {
        try {
            ProfesorResponse profesor = profesorService.obtenerPorId(id);
            if (profesor == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(profesor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crea un nuevo profesor
     * POST /api/gestion/profesores
     */
    @PostMapping
    public ResponseEntity<ProfesorResponse> crear(@RequestBody ProfesorRequest request) {
        try {
            ProfesorResponse profesor = profesorService.crear(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(profesor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Actualiza un profesor existente
     * PUT /api/gestion/profesores/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProfesorResponse> actualizar(
            @PathVariable Integer id,
            @RequestBody ProfesorRequest request) {
        try {
            ProfesorResponse profesor = profesorService.actualizar(id, request);
            if (profesor == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(profesor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Elimina un profesor
     * DELETE /api/gestion/profesores/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            boolean eliminado = profesorService.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
