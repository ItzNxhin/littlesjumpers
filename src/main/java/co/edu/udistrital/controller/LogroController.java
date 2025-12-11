package co.edu.udistrital.controller;

import co.edu.udistrital.model.Logro;
import co.edu.udistrital.service.LogroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/logros")
public class LogroController {

    @Autowired
    private LogroService logroService;

    /**
     * Obtiene todos los logros
     * GET /api/logros
     */
    @GetMapping
    public ResponseEntity<List<Logro>> listarTodos() {
        try {
            List<Logro> logros = logroService.obtenerTodos();
            return ResponseEntity.ok(logros);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene un logro por ID
     * GET /api/logros/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Logro> obtenerPorId(@PathVariable Integer id) {
        try {
            Optional<Logro> logro = logroService.obtenerPorId(id);
            return logro.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crea un nuevo logro
     * POST /api/logros
     */
    @PostMapping
    public ResponseEntity<Logro> crear(@RequestBody Logro logro) {
        try {
            Logro logroCreado = logroService.crear(logro);
            return ResponseEntity.status(HttpStatus.CREATED).body(logroCreado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Actualiza un logro existente
     * PUT /api/logros/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Logro> actualizar(@PathVariable Integer id, @RequestBody Logro logro) {
        try {
            Logro logroActualizado = logroService.actualizar(id, logro);
            if (logroActualizado == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(logroActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Elimina un logro
     * DELETE /api/logros/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            boolean eliminado = logroService.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
