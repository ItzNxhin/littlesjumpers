package co.edu.udistrital.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import co.edu.udistrital.dto.ProfesorRequest;
import co.edu.udistrital.dto.ProfesorResponse;
import co.edu.udistrital.model.Profesor;

public class ProfesorEntityMapper {

    /**
     * Convierte un ProfesorRequest (DTO) a una entidad Profesor.
     *
     * @param request DTO con los datos del profesor
     * @return Entidad Profesor mapeada
     */
    public static Profesor toEntity(ProfesorRequest request) {
        if (request == null) {
            return null;
        }

        Profesor profesor = new Profesor();

        // Mapeo de campos heredados de Usuario
        profesor.setNombre(request.getNombre());
        profesor.setApellido(request.getApellido());
        profesor.setCedula(request.getCedula());
        profesor.setCorreo(request.getCorreo());

        // Mapeo de campos propios de Profesor
        profesor.setTarjeta_profesional(request.getTarjeta_profesional());

        return profesor;
    }

    /**
     * Actualiza una entidad Profesor existente con los datos del request.
     * Útil para operaciones de actualización (PUT/PATCH).
     *
     * @param profesor Entidad existente a actualizar
     * @param request DTO con los nuevos datos
     */
    public static void updateEntity(Profesor profesor, ProfesorRequest request) {
        if (profesor == null || request == null) {
            return;
        }

        // Actualizar campos heredados de Usuario
        if (request.getNombre() != null) {
            profesor.setNombre(request.getNombre());
        }
        if (request.getApellido() != null) {
            profesor.setApellido(request.getApellido());
        }
        if (request.getCedula() != null) {
            profesor.setCedula(request.getCedula());
        }
        if (request.getCorreo() != null) {
            profesor.setCorreo(request.getCorreo());
        }

        // Actualizar campos propios de Profesor
        if (request.getTarjeta_profesional() != null) {
            profesor.setTarjeta_profesional(request.getTarjeta_profesional());
        }
    }

    /**
     * Convierte una entidad Profesor a ProfesorResponse.
     *
     * @param profesor Entidad Profesor
     * @return ProfesorResponse
     */
    public static ProfesorResponse toResponse(Profesor profesor) {
        if (profesor == null) {
            return null;
        }

        ProfesorResponse response = new ProfesorResponse(
            profesor.getNombre(),
            profesor.getApellido(),
            profesor.getCedula(),
            null
        );

        response.setCorreo(profesor.getCorreo());
        response.setTarjeta_profesional(profesor.getTarjeta_profesional());

        return response;
    }

    /**
     * Convierte una lista de entidades Profesor a una lista de ProfesorResponse.
     *
     * @param profesores Lista de entidades Profesor
     * @return Lista de ProfesorResponse
     */
    public static List<ProfesorResponse> toResponseList(List<Profesor> profesores) {
        
        if (profesores == null) {
            return null;
        }

        return profesores.stream()
                .map(ProfesorEntityMapper::toResponse)
                .collect(Collectors.toList());
    }
}
