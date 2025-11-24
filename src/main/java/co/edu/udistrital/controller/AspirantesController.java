package co.edu.udistrital.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.dto.UsuarioRequest;
import co.edu.udistrital.service.AspirantesService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/aspirantes")
@CrossOrigin(origins = "*")
public class AspirantesController {
    
    @Autowired
    private AspirantesService aspirantesService;

    @PostMapping("/existeAcudiente")
    public ResponseEntity<Boolean> postMethodName(@Valid @RequestBody UsuarioRequest usuarioRequest) {
        Boolean response = aspirantesService.existenciaAcudiente(usuarioRequest);
        
        return ResponseEntity.ok(response);
    }
    


}
