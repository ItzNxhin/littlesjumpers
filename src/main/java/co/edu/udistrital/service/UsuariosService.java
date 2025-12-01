package co.edu.udistrital.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.dto.AcudienteResponse;
import co.edu.udistrital.dto.EstudianteResponse;
import co.edu.udistrital.dto.ProfesorResponse;
import co.edu.udistrital.dto.mapper.AcudienteEntityMapper;
import co.edu.udistrital.dto.mapper.EstudianteEntityMapper;
import co.edu.udistrital.dto.mapper.ProfesorEntityMapper;
import co.edu.udistrital.exception.DatabaseException;
import co.edu.udistrital.model.Acudiente;
import co.edu.udistrital.model.Estudiante;
import co.edu.udistrital.model.Profesor;
import co.edu.udistrital.repository.AcudienteRepository;
import co.edu.udistrital.repository.EstudianteRepository;
import co.edu.udistrital.repository.ProfesorRepository;

@Service
public class UsuariosService {
    
    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private AcudienteRepository acudienteRepository;

    @Autowired
    private ProfesorRepository profesorRepository;


    //Lista de usuarios y estudiantes

    @Transactional(readOnly = true)
    public List<ProfesorResponse> obtenerProfesores(){
        try {
            List<Profesor> profesores = profesorRepository.findAll();
            return ProfesorEntityMapper.toResponseList(profesores);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al consultar estudiantes aspirantes", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener aspirantes: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<AcudienteResponse> obtenerAcudientes(){
        try {
            List<Acudiente> acudientes = acudienteRepository.findAll();
            return AcudienteEntityMapper.toResponseList(acudientes);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al consultar estudiantes aspirantes", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener aspirantes: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<EstudianteResponse> obtenerEstudiantes(){
        try {
            List<Estudiante> estudiantes = estudianteRepository.findAll();
            return EstudianteEntityMapper.toResponseList(estudiantes);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al consultar estudiantes aspirantes", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener aspirantes: " + e.getMessage(), e);
        }
    }
}
