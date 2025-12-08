package co.edu.udistrital.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.dto.EstudianteResponse;
import co.edu.udistrital.service.AcademicoProfesorService;

@RestController
@RequestMapping("/api/profesor")
@CrossOrigin(origins = "*")
public class ProfesorController {

    @Autowired
    private AcademicoProfesorService academicoProfesorService;

    /**
     * Obtiene estudiantes por grupo
     * GET /api/profesor/estudiantes/{profesorId}
     */
    @GetMapping("/estudiantes/{profesorId}")
    public ResponseEntity<List<EstudianteResponse>> listarPorGrupo(@PathVariable Integer profesorId) {
        try {
            List<EstudianteResponse> estudiantes = academicoProfesorService
                    .obtenerEstudiantesDelGrupoProfesor(profesorId);
            return ResponseEntity.ok(estudiantes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
