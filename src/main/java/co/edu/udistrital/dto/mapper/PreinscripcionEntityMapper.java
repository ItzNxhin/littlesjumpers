package co.edu.udistrital.dto.mapper;

import java.util.ArrayList;
import java.util.List;

import co.edu.udistrital.dto.PreinscripcionResponse;
import co.edu.udistrital.model.Preinscripcion;

public class PreinscripcionEntityMapper {

    /**
     * Convierte una entidad Preinscripcion a PreinscripcionResponse (DTO).
     *
     * @param preinscripcion Entidad Preinscripcion
     * @return PreinscripcionResponse con los datos de la preinscripción
     */
    public static PreinscripcionResponse toResponse(Preinscripcion preinscripcion) {
        return toResponse(preinscripcion, null);
    }

    /**
     * Convierte una entidad Preinscripcion a PreinscripcionResponse (DTO) con mensaje personalizado.
     *
     * @param preinscripcion Entidad Preinscripcion
     * @param message Mensaje personalizado para incluir en la respuesta
     * @return PreinscripcionResponse con los datos de la preinscripción
     */
    public static PreinscripcionResponse toResponse(Preinscripcion preinscripcion, String message) {
        if (preinscripcion == null) {
            return null;
        }

        PreinscripcionResponse response = new PreinscripcionResponse();

        response.setId(preinscripcion.getId());
        response.setFecha_solicitud(preinscripcion.getFecha_solicitud());
        response.setFecha_entrevista(preinscripcion.getFecha_entrevista());
        response.setEstado_entrevista(preinscripcion.getEstado_entrevista());

        // Mapear datos del estudiante si existe
        if (preinscripcion.getEstudiante() != null) {
            response.setEstudiante_id(preinscripcion.getEstudiante().getId());
            response.setEstudiante_nombre(preinscripcion.getEstudiante().getNombre());
            response.setEstudiante_apellido(preinscripcion.getEstudiante().getApellido());
        }

        response.setMessage(message);

        return response;
    }

    /**
     * Convierte una lista de entidades Preinscripcion a una lista de PreinscripcionResponse.
     *
     * @param preinscripciones Lista de entidades Preinscripcion
     * @return Lista de PreinscripcionResponse
     */
    public static List<PreinscripcionResponse> toResponseList(List<Preinscripcion> preinscripciones) {
        if (preinscripciones == null) {
            return null;
        }

        List<PreinscripcionResponse> responses = new ArrayList<>();
        for (Preinscripcion preinscripcion : preinscripciones) {
            responses.add(toResponse(preinscripcion));
        }

        return responses;
    }
}
