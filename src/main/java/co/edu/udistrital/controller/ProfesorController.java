package co.edu.udistrital.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.service.AcademicoProfesorService;

@RestController
@RequestMapping("/api/profesor")
@CrossOrigin(origins = "*")
public class ProfesorController {

    @Autowired
    private AcademicoProfesorService academicoProfesorService;
    
}
