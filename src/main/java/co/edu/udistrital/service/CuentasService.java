package co.edu.udistrital.service;

import co.edu.udistrital.dto.CuentaRequest;
import co.edu.udistrital.dto.CuentaResponse;
import co.edu.udistrital.model.Cuenta;
import co.edu.udistrital.model.Acudiente;
import co.edu.udistrital.model.Profesor;
import co.edu.udistrital.repository.CuentaRepository;
import co.edu.udistrital.repository.AcudienteRepository;
import co.edu.udistrital.repository.ProfesorRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CuentasService {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private AcudienteRepository acudienteRepository;

    @Autowired
    private ProfesorRepository profesorRepository;

    /**
     * Obtiene todas las cuentas
     */
    public List<CuentaResponse> obtenerTodas() {
        return cuentaRepository.findAll().stream()
                .map(CuentaResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una cuenta por ID
     */
    public CuentaResponse obtenerPorId(int id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElse(null);
        return cuenta != null ? new CuentaResponse(cuenta) : null;
    }

    /**
     * Crea una nueva cuenta
     */
    @Transactional
    public CuentaResponse crear(CuentaRequest request) {
        // Verificar que el username no exista
        if (cuentaRepository.existsByNombreUsuario(request.getNombreUsuario())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        // Validar rol
        Cuenta.TipoRol rol;
        try {
            rol = Cuenta.TipoRol.valueOf(request.getTipoRol());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rol inválido: " + request.getTipoRol());
        }

        // Crear cuenta
        Cuenta cuenta = new Cuenta();
        cuenta.setNombreUsuario(request.getNombreUsuario());
        cuenta.setContrasena(request.getContrasena());
        cuenta.setRol(rol);
        cuenta.setActivo(true);

        Cuenta guardada = cuentaRepository.save(cuenta);
        return new CuentaResponse(guardada);
    }

    /**
     * Asigna una cuenta a un profesor
     */
    @Transactional
    public void asignarCuentaAProfesor(int cuentaId, int profesorId) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        Profesor profesor = profesorRepository.findById(profesorId)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        // Verificar que la cuenta sea de tipo profesor
        if (cuenta.getRol() != Cuenta.TipoRol.profesor) {
            throw new RuntimeException("La cuenta no es de tipo profesor");
        }

        // Asignar cuenta al profesor
        profesor.setCuenta(cuenta);
        profesorRepository.save(profesor);
    }

    /**
     * Asigna una cuenta a un acudiente
     */
    @Transactional
    public void asignarCuentaAAcudiente(int cuentaId, int acudienteId) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        Acudiente acudiente = acudienteRepository.findById(acudienteId)
                .orElseThrow(() -> new RuntimeException("Acudiente no encontrado"));

        // Verificar que la cuenta sea de tipo acudiente
        if (cuenta.getRol() != Cuenta.TipoRol.acudiente) {
            throw new RuntimeException("La cuenta no es de tipo acudiente");
        }

        // Asignar cuenta al acudiente
        acudiente.setCuenta(cuenta);
        acudienteRepository.save(acudiente);
    }

    /**
     * Desactiva una cuenta
     */
    @Transactional
    public CuentaResponse desactivar(int id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        cuenta.setActivo(false);
        Cuenta actualizada = cuentaRepository.save(cuenta);
        return new CuentaResponse(actualizada);
    }

    /**
     * Activa una cuenta
     */
    @Transactional
    public CuentaResponse activar(int id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        cuenta.setActivo(true);
        Cuenta actualizada = cuentaRepository.save(cuenta);
        return new CuentaResponse(actualizada);
    }

    /**
     * Cambia la contraseña de una cuenta
     */
    @Transactional
    public void cambiarContrasena(int id, String nuevaContrasena) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        cuenta.setContrasena(nuevaContrasena);
        cuentaRepository.save(cuenta);
    }

    /**
     * Elimina una cuenta
     */
    @Transactional
    public boolean eliminar(int id) {
        if (!cuentaRepository.existsById(id)) {
            return false;
        }
        cuentaRepository.deleteById(id);
        return true;
    }
}
