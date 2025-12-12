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
import co.edu.udistrital.dto.mapper.EstudianteEntityMapper;
import co.edu.udistrital.exception.DatabaseException;
import co.edu.udistrital.model.Acudiente;
import co.edu.udistrital.model.Estudiante;
import co.edu.udistrital.model.Preinscripcion;
import co.edu.udistrital.model.Estudiante.Estado;
import co.edu.udistrital.repository.PreinscripcionRepository;
import co.edu.udistrital.repository.UsuariosRepository;

@Service
public class AspirantesService {

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private AcudienteService acudienteService;

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private PreinscripcionRepository preinscripcionRepository;

    @Autowired
    private HojaVidaService hojaVidaService;

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
            return acudienteService.crear(acudienteRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = false)
    public EstudianteResponse guardarEstudiante(EstudianteRequest estudianteRequest){
        try {
            // Buscar acudiente por cédula o por ID usando el servicio
            Optional<Acudiente> acudienteOpt;

            if (estudianteRequest.getAcudiente_cedula() != null && !estudianteRequest.getAcudiente_cedula().isEmpty()) {
                // Buscar por cédula
                acudienteOpt = acudienteService.obtenerEntidadPorCedula(estudianteRequest.getAcudiente_cedula());
                if(!acudienteOpt.isPresent()){
                    throw new RuntimeException("Acudiente no encontrado con cédula: " + estudianteRequest.getAcudiente_cedula());
                }
            } else if (estudianteRequest.getAcudiente_id() > 0) {
                // Buscar por ID
                acudienteOpt = acudienteService.obtenerEntidadPorId(estudianteRequest.getAcudiente_id());
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

            // Crear el estudiante usando EstudianteService (delegando la persistencia)
            EstudianteRequest requestParaServicio = new EstudianteRequest();
            requestParaServicio.setTarjeta_identidad(estudiante.getTarjeta_identidad());
            requestParaServicio.setNombre(estudiante.getNombre());
            requestParaServicio.setApellido(estudiante.getApellido());
            requestParaServicio.setFecha_nacimiento(estudiante.getFecha_nacimiento());
            requestParaServicio.setGrado_aplicado(estudiante.getGrado_aplicado());
            requestParaServicio.setEstado(estudiante.getEstado());
            requestParaServicio.setAcudiente_id(acudiente.getId());

            
            EstudianteResponse estudianteResponse = estudianteService.crear(requestParaServicio);

            // Obtener la entidad guardada para crear la preinscripción y la hoja de vida
            Optional<Estudiante> estudianteGuardadoOpt = estudianteService.obtenerEntidadPorId(estudianteResponse.getId());
            if (estudianteGuardadoOpt.isPresent()) {
                // Crear la Hoja de Vida
                hojaVidaService.obtenerOCrearPorEstudiante(estudianteGuardadoOpt.get());
                preinscripcionRepository.save(new Preinscripcion(estudianteGuardadoOpt.get()));
            }

            estudianteResponse.setMessage("Estudiante registrado con éxito");
            return estudianteResponse;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al guardar el estudiante en la base de datos", e);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar estudiante: " + e.getMessage(), e);
        }
    }

}
