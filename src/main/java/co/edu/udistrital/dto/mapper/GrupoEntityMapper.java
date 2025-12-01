package co.edu.udistrital.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import co.edu.udistrital.dto.GrupoRequest;
import co.edu.udistrital.dto.GrupoResponse;
import co.edu.udistrital.model.Grupo;
import co.edu.udistrital.model.Profesor;

public class GrupoEntityMapper {

    /**
     * Convierte un GrupoRequest (DTO) a una entidad Grupo.
     *
     * @param request DTO con los datos del grupo
     * @param profesor Profesor asociado al grupo (puede ser null)
     * @return Entidad Grupo mapeada
     */
    public static Grupo toEntity(GrupoRequest request, Profesor profesor) {
        if (request == null) {
            return null;
        }

        Grupo grupo = new Grupo();
        grupo.setGrado(request.getGrado());
        grupo.setIdentificador(request.getIdentificador());
        grupo.setCapacidad(request.getCapacidad() != null ? request.getCapacidad() : 20);

        if (profesor != null) {
            grupo.setProfesor(profesor);
        }

        return grupo;
    }

    /**
     * Convierte una entidad Grupo a GrupoResponse.
     *
     * @param grupo Entidad Grupo
     * @return GrupoResponse
     */
    public static GrupoResponse toResponse(Grupo grupo) {
        if (grupo == null) {
            return null;
        }

        GrupoResponse response = new GrupoResponse(
            grupo.getId(),
            grupo.getGrado(),
            grupo.getIdentificador()
        );

        response.setCapacidad(grupo.getCapacidad());

        if (grupo.getProfesor() != null) {
            response.setProfesor_id(grupo.getProfesor().getId());
            response.setProfesor_nombre(grupo.getProfesor().getNombre() + " " + grupo.getProfesor().getApellido());
        }

        return response;
    }

    /**
     * Actualiza una entidad Grupo existente con los datos del request.
     *
     * @param grupo Entidad existente a actualizar
     * @param request DTO con los nuevos datos
     * @param profesor Profesor a asignar (puede ser null)
     */
    public static void updateEntity(Grupo grupo, GrupoRequest request, Profesor profesor) {
        if (grupo == null || request == null) {
            return;
        }

        if (request.getGrado() != null) {
            grupo.setGrado(request.getGrado());
        }
        if (request.getIdentificador() != null) {
            grupo.setIdentificador(request.getIdentificador());
        }
        if (request.getCapacidad() != null) {
            grupo.setCapacidad(request.getCapacidad());
        }

        grupo.setProfesor(profesor);
    }

    /**
     * Convierte una lista de entidades Grupo a una lista de GrupoResponse.
     *
     * @param grupos Lista de entidades Grupo
     * @return Lista de GrupoResponse
     */
    public static List<GrupoResponse> toResponseList(List<Grupo> grupos) {
        if (grupos == null) {
            return null;
        }

        return grupos.stream()
                .map(GrupoEntityMapper::toResponse)
                .collect(Collectors.toList());
    }
}
