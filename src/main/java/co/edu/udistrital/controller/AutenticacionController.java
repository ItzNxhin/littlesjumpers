package co.edu.udistrital.controller;

import co.edu.udistrital.dto.LoginRequest;
import co.edu.udistrital.dto.LoginResponse;
import co.edu.udistrital.service.AutenticacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AutenticacionController {

    @Autowired
    private AutenticacionService autenticacionService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> iniciarSesion(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = autenticacionService.iniciarSesion(loginRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Servicio de autenticación funcionando correctamente");
    }
}
