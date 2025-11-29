package co.edu.udistrital.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.dto.AcudienteRequest;
import co.edu.udistrital.dto.AcudienteResponse;
import co.edu.udistrital.dto.EstudianteRequest;
import co.edu.udistrital.dto.EstudianteResponse;
import co.edu.udistrital.dto.UsuarioRequest;
import co.edu.udistrital.dto.mapper.AcudienteEntityMapper;
import co.edu.udistrital.dto.mapper.EstudianteEntityMapper;
import co.edu.udistrital.exception.DatabaseException;
import co.edu.udistrital.model.Acudiente;
import co.edu.udistrital.model.Estudiante;
import co.edu.udistrital.model.Preinscripcion;
import co.edu.udistrital.model.Estudiante.Estado;
import co.edu.udistrital.repository.AcudienteRepository;
import co.edu.udistrital.repository.EstudianteRepository;
import co.edu.udistrital.repository.PreinscripcionRepository;
import co.edu.udistrital.repository.UsuariosRepository;

@Service
public class AspirantesService {

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private AcudienteRepository acudienteRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private PreinscripcionRepository preinscripcionRepository;

    @Transactional(readOnly = true)
    public Boolean existenciaAcudiente(UsuarioRequest usuarioRequest) {
        try {
            System.out.println(usuarioRequest.getCedula());
            return usuariosRepository.existsByCedula(usuarioRequest.getCedula());
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al consultar la base de datos", e);
        } catch (Exception e) {
            throw new RuntimeException("Ha ocurrido un error: " + e.getMessage()+ " " + usuarioRequest.getCedula() + " " + usuariosRepository.findAll());
        }
    }

    @Transactional(readOnly = false)
    public AcudienteResponse guardarAcudiente(AcudienteRequest acudienteRequest){
        try {
            acudienteRepository.save(AcudienteEntityMapper.toEntity(acudienteRequest));
            return new AcudienteResponse(
                acudienteRequest.getNombre(),
                acudienteRequest.getApellido(),
                acudienteRequest.getCedula(),
                "Acudiente creado con éxito"
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = false)
    public EstudianteResponse guardarEstudiante(EstudianteRequest estudianteRequest){
        try {
            // Buscar acudiente por cédula o por ID
            Optional<Acudiente> acudienteOpt;

            if (estudianteRequest.getAcudiente_cedula() != null && !estudianteRequest.getAcudiente_cedula().isEmpty()) {
                // Buscar por cédula
                acudienteOpt = acudienteRepository.findByCedula(estudianteRequest.getAcudiente_cedula());
                if(!acudienteOpt.isPresent()){
                    throw new RuntimeException("Acudiente no encontrado con cédula: " + estudianteRequest.getAcudiente_cedula());
                }
            } else if (estudianteRequest.getAcudiente_id() > 0) {
                // Buscar por ID
                acudienteOpt = acudienteRepository.findById(estudianteRequest.getAcudiente_id());
                if(!acudienteOpt.isPresent()){
                    throw new RuntimeException("Acudiente no encontrado con ID: " + estudianteRequest.getAcudiente_id());
                }
            } else {
                throw new RuntimeException("Debe proporcionar la cédula o el ID del acudiente");
            }

            Acudiente acudiente = acudienteOpt.get();
            Estudiante estudiante = EstudianteEntityMapper.toEntity(estudianteRequest, acudiente);

            // Establecer estado inicial como aspirante si no viene definido
            if(estudiante.getEstado() == null){
                estudiante.setEstado(Estado.aspirante);
            }

            Estudiante estudianteGuardado = estudianteRepository.save(estudiante);
            preinscripcionRepository.save(new Preinscripcion(estudianteGuardado));


            EstudianteResponse response = new EstudianteResponse();
            response.setId(estudianteGuardado.getId());
            response.setTarjeta_identidad(estudianteGuardado.getTarjeta_identidad());
            response.setNombre(estudianteGuardado.getNombre());
            response.setApellido(estudianteGuardado.getApellido());
            response.setFecha_nacimiento(estudianteGuardado.getFecha_nacimiento());
            response.setGrado_aplicado(estudianteGuardado.getGrado_aplicado());
            response.setEstado(estudianteGuardado.getEstado());
            response.setGrupo(estudianteGuardado.getGrupo());
            response.setAcudiente_id(estudianteGuardado.getAcudiente().getId());
            response.setMessage("Estudiante registrado con éxito");

            return response;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al guardar el estudiante en la base de datos", e);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar estudiante: " + e.getMessage(), e);
        }
    }

}
