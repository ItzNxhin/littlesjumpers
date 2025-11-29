package co.edu.udistrital.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.dto.AcudienteRequest;
import co.edu.udistrital.dto.AcudienteResponse;
import co.edu.udistrital.dto.EstudianteRequest;
import co.edu.udistrital.dto.EstudianteResponse;
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

    /**
     * Metodo controlador para comunicar la existencia o no de un acudiente
     * 
     * @param usuarioRequest
     * @return
     */
    @PostMapping("/existeAcudiente")
    public ResponseEntity<Boolean> existenciaAcudiente(@Valid @RequestBody UsuarioRequest usuarioRequest) {
        Boolean response = aspirantesService.existenciaAcudiente(usuarioRequest);

        return ResponseEntity.ok(response);
    }

    /**
     * Método controlador para mandar a la capa de servicios el guardado de un
     * acudiente aspirante y mandar respuesta
     *
     * @param acudienteRequest
     * @return
     */
    @PostMapping("/registrarAcudiente")
    public ResponseEntity<AcudienteResponse> registrarAcudiente(@Valid @RequestBody AcudienteRequest acudienteRequest) {
        try {
            AcudienteResponse response = aspirantesService.guardarAcudiente(acudienteRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            AcudienteResponse errorResponse = new AcudienteResponse(
                acudienteRequest.getNombre(),
                acudienteRequest.getApellido(),
                acudienteRequest.getCedula(),
                "Error al registrar acudiente: " + e.getMessage()
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Método controlador para mandar a la capa de servicios el guardado de un
     * estudiante aspirante y mandar respuesta
     *
     * @param estudianteRequest
     * @return
     */
    @PostMapping("/registrarEstudiante")
    public ResponseEntity<EstudianteResponse> registrarEstudiante(@Valid @RequestBody EstudianteRequest estudianteRequest) {
        try {
            EstudianteResponse response = aspirantesService.guardarEstudiante(estudianteRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            EstudianteResponse errorResponse = new EstudianteResponse();
            errorResponse.setMessage("Error al registrar estudiante: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

}
