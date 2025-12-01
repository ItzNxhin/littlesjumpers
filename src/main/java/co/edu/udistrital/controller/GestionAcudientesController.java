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

import co.edu.udistrital.dto.AcudienteRequest;
import co.edu.udistrital.dto.AcudienteResponse;
import co.edu.udistrital.service.AcudienteService;

@RestController
@RequestMapping("/api/gestion/acudientes")
public class GestionAcudientesController {

    @Autowired
    private AcudienteService acudienteService;

    /**
     * Obtiene todos los acudientes
     * GET /api/gestion/acudientes
     */
    @GetMapping
    public ResponseEntity<List<AcudienteResponse>> listarTodos() {
        try {
            List<AcudienteResponse> acudientes = acudienteService.obtenerTodos();
            return ResponseEntity.ok(acudientes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene un acudiente por ID
     * GET /api/gestion/acudientes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AcudienteResponse> obtenerPorId(@PathVariable Integer id) {
        try {
            AcudienteResponse acudiente = acudienteService.obtenerPorId(id);
            if (acudiente == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(acudiente);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene un acudiente por cédula
     * GET /api/gestion/acudientes/cedula/{cedula}
     */
    @GetMapping("/cedula/{cedula}")
    public ResponseEntity<AcudienteResponse> obtenerPorCedula(@PathVariable String cedula) {
        try {
            AcudienteResponse acudiente = acudienteService.obtenerPorCedula(cedula);
            if (acudiente == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(acudiente);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crea un nuevo acudiente
     * POST /api/gestion/acudientes
     */
    @PostMapping
    public ResponseEntity<AcudienteResponse> crear(@RequestBody AcudienteRequest request) {
        try {
            AcudienteResponse acudiente = acudienteService.crear(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(acudiente);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Actualiza un acudiente existente
     * PUT /api/gestion/acudientes/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<AcudienteResponse> actualizar(
            @PathVariable Integer id,
            @RequestBody AcudienteRequest request) {
        try {
            AcudienteResponse acudiente = acudienteService.actualizar(id, request);
            if (acudiente == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(acudiente);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Elimina un acudiente
     * DELETE /api/gestion/acudientes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            boolean eliminado = acudienteService.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
