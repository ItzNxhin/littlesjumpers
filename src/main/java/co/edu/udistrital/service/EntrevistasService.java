package co.edu.udistrital.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.dto.EstudianteResponse;
import co.edu.udistrital.dto.PreinscripcionResponse;
import co.edu.udistrital.dto.mapper.PreinscripcionEntityMapper;
import co.edu.udistrital.exception.DatabaseException;
import co.edu.udistrital.model.Estudiante;
import co.edu.udistrital.model.Estudiante.Estado;
import co.edu.udistrital.model.Preinscripcion;
import co.edu.udistrital.model.Preinscripcion.EstadoEntrevista;
import co.edu.udistrital.repository.PreinscripcionRepository;

@Service
public class EntrevistasService {

    @Autowired
    private EstudianteService estudianteService;

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
            return estudianteService.obtenerPorEstado(Estado.aspirante);
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
            EstudianteResponse estudianteResponse = estudianteService.obtenerPorId(estudianteId);
            if (estudianteResponse == null) {
                throw new RuntimeException("Estudiante no encontrado con ID: " + estudianteId);
            }

            // Verificar que el estudiante es aspirante
            if (estudianteResponse.getEstado() != Estado.aspirante) {
                throw new RuntimeException("El estudiante no es aspirante");
            }

            // Obtener la entidad completa para guardar la relación
            Optional<Estudiante> estudianteOpt = estudianteService.obtenerEntidadPorId(estudianteId);
            if (!estudianteOpt.isPresent()) {
                throw new RuntimeException("Error al obtener entidad estudiante");
            }

            Estudiante estudiante = estudianteOpt.get();

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

            String asunto_email = "Entrevista programada";

            emailService.enviarEmail(updated.getEstudiante().getAcudiente().getCorreo(), asunto_email, mensaje_email);

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
            EstudianteResponse estudianteResponse = estudianteService.obtenerPorId(estudianteId);

            if (estudianteResponse == null) {
                throw new RuntimeException("Estudiante no encontrado con ID: " + estudianteId);
            }

            if (estudianteResponse.getEstado() != Estado.aspirante) {
                throw new RuntimeException(
                        "Solo se pueden aceptar estudiantes aspirantes. Estado actual: "
                                + estudianteResponse.getEstado());
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

            // Aceptar estudiante usando el servicio
            EstudianteResponse response = estudianteService.actualizarEstado(estudianteId, Estado.aceptado);

            // Obtener la entidad completa para acceder al acudiente
            Optional<Estudiante> estudianteOpt = estudianteService.obtenerEntidadPorId(estudianteId);
            if (estudianteOpt.isPresent()) {
                Estudiante estudiante = estudianteOpt.get();
                String mensaje_email = "Hola: " + estudiante.getAcudiente().getNombre()
                        + "! \nLe queremos informar que  su hijo " + estudiante.getNombre()
                        + " " + estudiante.getApellido() + " ha sido aceptado en nuestra institución, ¡Bienvenidos!";

                String asunto_email = "Resultado de admisión: ¡Su hijo ha sido aceptado!";

                emailService.enviarEmail(estudiante.getAcudiente().getCorreo(), asunto_email, mensaje_email);
            }

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
            EstudianteResponse estudianteResponse = estudianteService.obtenerPorId(estudianteId);

            if (estudianteResponse == null) {
                throw new RuntimeException("Estudiante no encontrado con ID: " + estudianteId);
            }

            if (estudianteResponse.getEstado() != Estado.aspirante) {
                throw new RuntimeException(
                        "Solo se pueden rechazar estudiantes aspirantes. Estado actual: "
                                + estudianteResponse.getEstado());
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

            // Rechazar estudiante usando el servicio
            EstudianteResponse response = estudianteService.actualizarEstado(estudianteId, Estado.rechazado);

            // Obtener la entidad completa para acceder al acudiente
            Optional<Estudiante> estudianteOpt = estudianteService.obtenerEntidadPorId(estudianteId);
            if (estudianteOpt.isPresent()) {
                Estudiante estudiante = estudianteOpt.get();
                String mensaje_email = "Hola: " + estudiante.getAcudiente().getNombre()
                        + "! \nLamentamos informar que  su hijo " + estudiante.getNombre()
                        + " " + estudiante.getApellido() + " ha sido rechazado en el proceso.";

                String asunto_email = "Resultado de admisión: Lo sentimos...";

                emailService.enviarEmail(estudiante.getAcudiente().getCorreo(), asunto_email, mensaje_email);
            }

            response.setMessage("Estudiante rechazado");
            return response;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al rechazar estudiante", e);
        }
    }
}
