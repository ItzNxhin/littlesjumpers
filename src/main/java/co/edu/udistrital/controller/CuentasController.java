package co.edu.udistrital.controller;

import co.edu.udistrital.dto.AsignarCuentaRequest;
import co.edu.udistrital.dto.CuentaRequest;
import co.edu.udistrital.dto.CuentaResponse;
import co.edu.udistrital.service.CuentasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gestion/cuentas")
public class CuentasController {

    @Autowired
    private CuentasService cuentasService;

    /**
     * Obtiene todas las cuentas
     * GET /api/gestion/cuentas
     */
    @GetMapping
    public ResponseEntity<List<CuentaResponse>> listarTodas() {
        try {
            List<CuentaResponse> cuentas = cuentasService.obtenerTodas();
            return ResponseEntity.ok(cuentas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene una cuenta por ID
     * GET /api/gestion/cuentas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponse> obtenerPorId(@PathVariable int id) {
        try {
            CuentaResponse cuenta = cuentasService.obtenerPorId(id);
            if (cuenta == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(cuenta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crea una nueva cuenta
     * POST /api/gestion/cuentas
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CuentaRequest request) {
        try {
            CuentaResponse cuenta = cuentasService.crear(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(cuenta);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al crear la cuenta");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Asigna una cuenta a un usuario (profesor o acudiente)
     * POST /api/gestion/cuentas/asignar
     */
    @PostMapping("/asignar")
    public ResponseEntity<?> asignarCuenta(@RequestBody AsignarCuentaRequest request) {
        try {
            if ("profesor".equalsIgnoreCase(request.getTipoUsuario())) {
                cuentasService.asignarCuentaAProfesor(request.getCuentaId(), request.getUsuarioId());
            } else if ("acudiente".equalsIgnoreCase(request.getTipoUsuario())) {
                cuentasService.asignarCuentaAAcudiente(request.getCuentaId(), request.getUsuarioId());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Tipo de usuario inválido");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Cuenta asignada correctamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al asignar la cuenta");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Desactiva una cuenta
     * PUT /api/gestion/cuentas/{id}/desactivar
     */
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivar(@PathVariable int id) {
        try {
            CuentaResponse cuenta = cuentasService.desactivar(id);
            return ResponseEntity.ok(cuenta);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Activa una cuenta
     * PUT /api/gestion/cuentas/{id}/activar
     */
    @PutMapping("/{id}/activar")
    public ResponseEntity<?> activar(@PathVariable int id) {
        try {
            CuentaResponse cuenta = cuentasService.activar(id);
            return ResponseEntity.ok(cuenta);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cambia la contraseña de una cuenta
     * PUT /api/gestion/cuentas/{id}/contrasena
     */
    @PutMapping("/{id}/contrasena")
    public ResponseEntity<?> cambiarContrasena(@PathVariable int id, @RequestBody Map<String, String> body) {
        try {
            String nuevaContrasena = body.get("nuevaContrasena");
            if (nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "La contraseña no puede estar vacía");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            cuentasService.cambiarContrasena(id, nuevaContrasena);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Contraseña actualizada correctamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al cambiar la contraseña");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Elimina una cuenta
     * DELETE /api/gestion/cuentas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        try {
            boolean eliminado = cuentasService.eliminar(id);
            if (!eliminado) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
