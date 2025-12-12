package co.edu.udistrital.service;

import co.edu.udistrital.dto.CitacionRequest;
import co.edu.udistrital.dto.CitacionResponse;
import co.edu.udistrital.dto.UsuarioSimpleResponse;
import co.edu.udistrital.model.Citacion;
import co.edu.udistrital.model.CitacionDestinatario;
import co.edu.udistrital.model.Usuario;
import co.edu.udistrital.repository.CitacionDestinatarioRepository;
import co.edu.udistrital.repository.CitacionRepository;
import co.edu.udistrital.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitacionService {

    @Autowired
    private CitacionRepository citacionRepository;

    @Autowired
    private CitacionDestinatarioRepository citacionDestinatarioRepository;

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Obtiene todos los usuarios disponibles para citaciones
     */
    @Transactional(readOnly = true)
    public List<UsuarioSimpleResponse> obtenerTodosLosUsuarios() {
        try {
            List<Usuario> usuarios = usuariosRepository.findAll();
            return usuarios.stream()
                    .map(UsuarioSimpleResponse::new)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al obtener usuarios", e);
        }
    }

    /**
     * Crea una citación y envía los emails de forma asíncrona
     */
    @Transactional
    public CitacionResponse crearCitacion(CitacionRequest request, Integer adminId) {
        try {
            // Validar tipo de citación
            Citacion.TipoCitacion tipo;
            try {
                tipo = Citacion.TipoCitacion.valueOf(request.getTipo());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Tipo de citación inválido: " + request.getTipo());
            }

            // Crear la citación
            Citacion citacion = new Citacion(
                    tipo,
                    request.getAsunto(),
                    request.getCuerpo(),
                    request.getFechaReunion(),
                    adminId
            );

            Citacion citacionGuardada = citacionRepository.save(citacion);

            // Obtener destinatarios
            List<Usuario> destinatarios;
            if (tipo == Citacion.TipoCitacion.masiva) {
                // Enviar a todos los usuarios
                destinatarios = usuariosRepository.findAll();
            } else {
                // Enviar solo a usuarios seleccionados
                if (request.getDestinatariosIds() == null || request.getDestinatariosIds().isEmpty()) {
                    throw new RuntimeException("Debe seleccionar al menos un destinatario para citación selectiva");
                }
                destinatarios = usuariosRepository.findAllById(request.getDestinatariosIds());
            }

            // Guardar destinatarios en la tabla intermedia
            for (Usuario usuario : destinatarios) {
                CitacionDestinatario destinatario = new CitacionDestinatario(
                        citacionGuardada.getId(),
                        usuario.getId()
                );
                citacionDestinatarioRepository.save(destinatario);
            }

            // Enviar emails de forma asíncrona
            enviarEmailsAsync(citacionGuardada, destinatarios);

            CitacionResponse response = new CitacionResponse(citacionGuardada);
            response.setCantidadDestinatarios(destinatarios.size());
            return response;
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al crear citación", e);
        }
    }

    /**
     * Envía los emails de forma asíncrona
     */
    @Async
    protected void enviarEmailsAsync(Citacion citacion, List<Usuario> destinatarios) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaFormateada = citacion.getFechaReunion().format(formatter);

        String mensajeEmail = construirMensajeEmail(citacion, fechaFormateada);

        for (Usuario usuario : destinatarios) {
            if (usuario.getCorreo() != null && !usuario.getCorreo().isEmpty()) {
                try {
                    emailService.enviarEmail(
                            usuario.getCorreo(),
                            citacion.getAsunto(),
                            mensajeEmail
                    );
                } catch (Exception e) {
                    // Log error but don't fail the whole process
                    System.err.println("Error enviando email a " + usuario.getCorreo() + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Construye el mensaje del email
     */
    private String construirMensajeEmail(Citacion citacion, String fechaFormateada) {
        return "Estimado/a usuario/a,\n\n" +
                citacion.getCuerpo() + "\n\n" +
                "Fecha de la reunión: " + fechaFormateada + "\n\n" +
                "Por favor, confirme su asistencia.\n\n" +
                "Atentamente,\n" +
                "Little Jumpers - Administración";
    }

    /**
     * Obtiene todas las citaciones
     */
    @Transactional(readOnly = true)
    public List<CitacionResponse> obtenerTodasLasCitaciones() {
        try {
            List<Citacion> citaciones = citacionRepository.findAllByOrderByFechaEnvioDesc();
            return citaciones.stream()
                    .map(c -> {
                        CitacionResponse response = new CitacionResponse(c);
                        int cantidad = citacionDestinatarioRepository.findByCitacionId(c.getId()).size();
                        response.setCantidadDestinatarios(cantidad);
                        return response;
                    })
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al obtener citaciones", e);
        }
    }

    /**
     * Obtiene una citación por ID
     */
    @Transactional(readOnly = true)
    public CitacionResponse obtenerPorId(Integer id) {
        try {
            return citacionRepository.findById(id)
                    .map(c -> {
                        CitacionResponse response = new CitacionResponse(c);
                        int cantidad = citacionDestinatarioRepository.findByCitacionId(c.getId()).size();
                        response.setCantidadDestinatarios(cantidad);
                        return response;
                    })
                    .orElse(null);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al obtener citación", e);
        }
    }
}
