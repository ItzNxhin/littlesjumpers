package co.edu.udistrital.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import co.edu.udistrital.dto.EstudianteRequest;
import co.edu.udistrital.dto.EstudianteResponse;
import co.edu.udistrital.model.Estudiante;
import co.edu.udistrital.model.Acudiente;

public class EstudianteEntityMapper {

    /**
     * Convierte un EstudianteRequest (DTO) a una entidad Estudiante.
     *
     * @param request DTO con los datos del estudiante
     * @param acudiente Acudiente asociado al estudiante (debe existir previamente)
     * @return Entidad Estudiante mapeada
     */
    public static Estudiante toEntity(EstudianteRequest request, Acudiente acudiente) {
        if (request == null) {
            return null;
        }

        Estudiante estudiante = new Estudiante();

        // Mapeo de campos básicos
        estudiante.setTarjeta_identidad(request.getTarjeta_identidad());
        estudiante.setNombre(request.getNombre());
        estudiante.setApellido(request.getApellido());
        estudiante.setFecha_nacimiento(request.getFecha_nacimiento());
        estudiante.setGrado_aplicado(request.getGrado_aplicado());
        estudiante.setEstado(request.getEstado());
        // Mapeo de relaciones
        if (acudiente != null) {
            estudiante.setAcudiente(acudiente);
        }

        return estudiante;
    }

    /**
     * Convierte una entidad Estudiante a EstudianteResponse (DTO).
     *
     * @param estudiante Entidad Estudiante
     * @return EstudianteResponse con los datos del estudiante
     */
    public static EstudianteResponse toResponse(Estudiante estudiante) {
        if (estudiante == null) {
            return null;
        }

        EstudianteResponse response = new EstudianteResponse();

        response.setId(estudiante.getId());
        response.setTarjeta_identidad(estudiante.getTarjeta_identidad());
        response.setNombre(estudiante.getNombre());
        response.setApellido(estudiante.getApellido());
        response.setFecha_nacimiento(estudiante.getFecha_nacimiento());
        response.setGrado_aplicado(estudiante.getGrado_aplicado());
        response.setEstado(estudiante.getEstado());

        if(estudiante.getGrupo()!= null)
            response.setGrupo_id(estudiante.getGrupo().getId());

        // Mapear ID del acudiente si existe
        if (estudiante.getAcudiente() != null) {
            response.setAcudiente_id(estudiante.getAcudiente().getId());
        }

        return response;
    }

    /**
     * Convierte una lista de entidades Estudiante a una lista de EstudianteResponse.
     *
     * @param estudiantes Lista de entidades Estudiante
     * @return Lista de EstudianteResponse
     */
    public static List<EstudianteResponse> toResponseList(List<Estudiante> estudiantes) {
        if (estudiantes == null) {
            return null;
        }

        return estudiantes.stream()
                .map(EstudianteEntityMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza una entidad Estudiante existente con los datos del request.
     * Útil para operaciones de actualización (PUT/PATCH).
     *
     * @param estudiante Entidad existente a actualizar
     * @param request DTO con los nuevos datos
     */
    public static void updateEntity(Estudiante estudiante, EstudianteRequest request) {
        if (estudiante == null || request == null) {
            return;
        }

        // Actualizar campos básicos
        if (request.getTarjeta_identidad() != null) {
            estudiante.setTarjeta_identidad(request.getTarjeta_identidad());
        }
        if (request.getNombre() != null) {
            estudiante.setNombre(request.getNombre());
        }
        if (request.getApellido() != null) {
            estudiante.setApellido(request.getApellido());
        }
        if (request.getFecha_nacimiento() != null) {
            estudiante.setFecha_nacimiento(request.getFecha_nacimiento());
        }
        if (request.getGrado_aplicado() != null) {
            estudiante.setGrado_aplicado(request.getGrado_aplicado());
        }
        if (request.getEstado() != null) {
            estudiante.setEstado(request.getEstado());
        }
    }
}