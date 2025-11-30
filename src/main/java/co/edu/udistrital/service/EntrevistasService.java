package co.edu.udistrital.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.dto.EstudianteResponse;
import co.edu.udistrital.dto.PreinscripcionResponse;
import co.edu.udistrital.dto.mapper.EstudianteEntityMapper;
import co.edu.udistrital.dto.mapper.PreinscripcionEntityMapper;
import co.edu.udistrital.exception.DatabaseException;
import co.edu.udistrital.model.Estudiante;
import co.edu.udistrital.model.Estudiante.Estado;
import co.edu.udistrital.model.Preinscripcion;
import co.edu.udistrital.model.Preinscripcion.EstadoEntrevista;
import co.edu.udistrital.repository.EstudianteRepository;
import co.edu.udistrital.repository.PreinscripcionRepository;

@Service
public class EntrevistasService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private PreinscripcionRepository preinscripcionRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Obtener todos los estudiantes aspirantes
     */
    @Transactional(readOnly = true)
    public List<EstudianteResponse> estudiantesAspirantes() {
        try {
            Optional<List<Estudiante>> estudiantes = estudianteRepository.findByEstado(Estado.aspirante);

            if (estudiantes.isPresent()) {
                return EstudianteEntityMapper.toResponseList(estudiantes.get());
            }
            return new ArrayList<>();
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al consultar estudiantes aspirantes", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener aspirantes: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener todas las preinscripciones
     */
    @Transactional(readOnly = true)
    public List<PreinscripcionResponse> obtenerTodasPreinscripciones() {
        try {
            List<Preinscripcion> preinscripciones = preinscripcionRepository.findAll();
            return PreinscripcionEntityMapper.toResponseList(preinscripciones);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al consultar preinscripciones", e);
        }
    }

    /**
     * Obtener preinscripciones por estado de entrevista
     */
    @Transactional(readOnly = true)
    public List<PreinscripcionResponse> obtenerPreinscripcionesPorEstado(EstadoEntrevista estado) {
        try {
            List<Preinscripcion> preinscripciones = preinscripcionRepository.findByEstado(estado);
            return PreinscripcionEntityMapper.toResponseList(preinscripciones);
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al consultar preinscripciones por estado", e);
        }
    }

    /**
     * Crear preinscripción para un estudiante aspirante
     */
    @Transactional(readOnly = false)
    public PreinscripcionResponse crearPreinscripcion(Integer estudianteId) {
        try {
            // Verificar que el estudiante existe
            Optional<Estudiante> estudianteOpt = estudianteRepository.findById(estudianteId);
            if (!estudianteOpt.isPresent()) {
                throw new RuntimeException("Estudiante no encontrado con ID: " + estudianteId);
            }

            Estudiante estudiante = estudianteOpt.get();

            // Verificar que el estudiante es aspirante
            if (estudiante.getEstado() != Estado.aspirante) {
                throw new RuntimeException("El estudiante no es aspirante");
            }

            // Verificar que no existe ya una preinscripción
            if (preinscripcionRepository.existsByEstudianteId(estudianteId)) {
                throw new RuntimeException("Ya existe una preinscripción para este estudiante");
            }

            // Crear nueva preinscripción
            Preinscripcion preinscripcion = new Preinscripcion();
            preinscripcion.setEstudiante(estudiante);
            preinscripcion.setFecha_solicitud(LocalDateTime.now());
            preinscripcion.setEstado(EstadoEntrevista.pendiente);

            Preinscripcion saved = preinscripcionRepository.save(preinscripcion);

            return PreinscripcionEntityMapper.toResponse(saved, "Preinscripción creada exitosamente");
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al crear preinscripción", e);
        }
    }

    /**
     * Programar entrevista para una preinscripción
     */
    @Transactional(readOnly = false)
    public PreinscripcionResponse programarEntrevista(Integer preinscripcionId, LocalDateTime fechaEntrevista) {
        try {
            Optional<Preinscripcion> preinscripcionOpt = preinscripcionRepository.findById(preinscripcionId);

            if (!preinscripcionOpt.isPresent()) {
                throw new RuntimeException("Preinscripción no encontrada con ID: " + preinscripcionId);
            }

            Preinscripcion preinscripcion = preinscripcionOpt.get();

            // Validar que la fecha de entrevista es futura
            if (fechaEntrevista.isBefore(LocalDateTime.now())) {
                throw new RuntimeException("La fecha de entrevista debe ser futura");
            }

            preinscripcion.setFecha_entrevista(fechaEntrevista);
            preinscripcion.setEstado(EstadoEntrevista.programada);

            Preinscripcion updated = preinscripcionRepository.save(preinscripcion);

            String mensaje_email = "Hola: " + updated.getEstudiante().getAcudiente().getNombre()
                    + "! \nLe queremos informar que la entrevista de su hijo " + updated.getEstudiante().getNombre()
                    + " " + updated.getEstudiante().getApellido() + " ha sido programa para la fecha y hora: "
                    + updated.getFecha_entrevista().toString();

            // TODO Revisar emails
            emailService.enviarEmail(mensaje_email, mensaje_email, mensaje_email);

            return PreinscripcionEntityMapper.toResponse(updated, "Entrevista programada exitosamente");
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al programar entrevista", e);
        }
    }

    /**
     * Marcar entrevista como realizada
     */
    @Transactional(readOnly = false)
    public PreinscripcionResponse marcarEntrevistaRealizada(Integer preinscripcionId) {
        try {
            Optional<Preinscripcion> preinscripcionOpt = preinscripcionRepository.findById(preinscripcionId);

            if (!preinscripcionOpt.isPresent()) {
                throw new RuntimeException("Preinscripción no encontrada con ID: " + preinscripcionId);
            }

            Preinscripcion preinscripcion = preinscripcionOpt.get();

            if (preinscripcion.getEstado() != EstadoEntrevista.programada) {
                throw new RuntimeException("La entrevista debe estar programada para marcarla como realizada");
            }

            preinscripcion.setEstado(EstadoEntrevista.realizada);

            Preinscripcion updated = preinscripcionRepository.save(preinscripcion);

            return PreinscripcionEntityMapper.toResponse(updated, "Entrevista marcada como realizada");
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al marcar entrevista como realizada", e);
        }
    }

    /**
     * Aceptar estudiante (cambiar estado de aspirante a aceptado)
     */
    @Transactional(readOnly = false)
    public EstudianteResponse aceptarEstudiante(Integer estudianteId) {
        try {
            Optional<Estudiante> estudianteOpt = estudianteRepository.findById(estudianteId);

            if (!estudianteOpt.isPresent()) {
                throw new RuntimeException("Estudiante no encontrado con ID: " + estudianteId);
            }

            Estudiante estudiante = estudianteOpt.get();

            if (estudiante.getEstado() != Estado.aspirante) {
                throw new RuntimeException(
                        "Solo se pueden aceptar estudiantes aspirantes. Estado actual: " + estudiante.getEstado());
            }

            // Verificar que tiene preinscripción
            Optional<Preinscripcion> preinscripcionOpt = preinscripcionRepository.findByEstudianteId(estudianteId);
            if (!preinscripcionOpt.isPresent()) {
                throw new RuntimeException("El estudiante no tiene una preinscripción registrada");
            }

            Preinscripcion preinscripcion = preinscripcionOpt.get();

            // Verificar que la entrevista está realizada
            if (preinscripcion.getEstado() != EstadoEntrevista.realizada) {
                throw new RuntimeException(
                        "La entrevista debe estar realizada antes de aceptar al estudiante. Estado actual: "
                                + preinscripcion.getEstado());
            }

            // Aceptar estudiante
            estudiante.setEstado(Estado.aceptado);
            Estudiante updated = estudianteRepository.save(estudiante);

            String mensaje_email = "Hola: " + updated.getAcudiente().getNombre()
                    + "! \nLe queremos informar que  su hijo " + updated.getNombre()
                    + " " + updated.getNombre() + "ha sido aceptado en nuestra institución, ¡Bienvenidos!";

            // TODO Revisar emails
            emailService.enviarEmail(mensaje_email, mensaje_email, mensaje_email);

            EstudianteResponse response = EstudianteEntityMapper.toResponse(updated);
            response.setMessage("Estudiante aceptado exitosamente");

            return response;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al aceptar estudiante", e);
        }
    }

    /**
     * Rechazar estudiante (cambiar estado de aspirante a rechazado)
     */
    @Transactional(readOnly = false)
    public EstudianteResponse rechazarEstudiante(Integer estudianteId) {
        try {
            Optional<Estudiante> estudianteOpt = estudianteRepository.findById(estudianteId);

            if (!estudianteOpt.isPresent()) {
                throw new RuntimeException("Estudiante no encontrado con ID: " + estudianteId);
            }

            Estudiante estudiante = estudianteOpt.get();

            if (estudiante.getEstado() != Estado.aspirante) {
                throw new RuntimeException(
                        "Solo se pueden rechazar estudiantes aspirantes. Estado actual: " + estudiante.getEstado());
            }

            // Verificar que tiene preinscripción
            Optional<Preinscripcion> preinscripcionOpt = preinscripcionRepository.findByEstudianteId(estudianteId);
            if (!preinscripcionOpt.isPresent()) {
                throw new RuntimeException("El estudiante no tiene una preinscripción registrada");
            }

            Preinscripcion preinscripcion = preinscripcionOpt.get();

            // Verificar que la entrevista está realizada
            if (preinscripcion.getEstado() != EstadoEntrevista.realizada) {
                throw new RuntimeException(
                        "La entrevista debe estar realizada antes de rechazar al estudiante. Estado actual: "
                                + preinscripcion.getEstado());
            }

            // Rechazar estudiante
            estudiante.setEstado(Estado.rechazado);
            Estudiante updated = estudianteRepository.save(estudiante);

            String mensaje_email = "Hola: " + updated.getAcudiente().getNombre()
                    + "! \nLamentamos informar que  su hijo " + updated.getNombre()
                    + " " + updated.getNombre() + "ha sido rechazado en el proceso.";

            // TODO Revisar emails
            emailService.enviarEmail(mensaje_email, mensaje_email, mensaje_email);

            EstudianteResponse response = EstudianteEntityMapper.toResponse(updated);
            response.setMessage("Estudiante rechazado");

            return response;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al rechazar estudiante", e);
        }
    }
}
