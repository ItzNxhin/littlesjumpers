package co.edu.udistrital.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.dto.AcudienteRequest;
import co.edu.udistrital.dto.AcudienteResponse;
import co.edu.udistrital.dto.UsuarioRequest;
import co.edu.udistrital.dto.mapper.AcudienteEntityMapper;
import co.edu.udistrital.exception.DatabaseException;
import co.edu.udistrital.repository.AcudienteRepository;
import co.edu.udistrital.repository.UsuariosRepository;

@Service
public class AspirantesService {

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private AcudienteRepository acudienteRepository;

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
}
