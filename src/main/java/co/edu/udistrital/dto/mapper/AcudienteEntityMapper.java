package co.edu.udistrital.dto.mapper;

import co.edu.udistrital.dto.AcudienteRequest;
import co.edu.udistrital.model.Acudiente;
public class AcudienteEntityMapper {

    /**
     * Convierte un AcudienteRequest (DTO) a una entidad Acudiente.
     *
     * @param request DTO con los datos del acudiente
     * @param cuenta Cuenta asociada al acudiente (debe existir previamente)
     * @return Entidad Acudiente mapeada
     */
    public static Acudiente toEntity(AcudienteRequest request) {
        if (request == null) {
            return null;
        }

        Acudiente acudiente = new Acudiente();

        // Mapeo de campos heredados de Usuario
        acudiente.setNombre(request.getNombre());
        acudiente.setApellido(request.getApellido());
        acudiente.setCedula(request.getCedula());
        acudiente.setCorreo(request.getCorreo());

        // Mapeo de campos propios de Acudiente
        acudiente.setContacto_extra(request.getContacto_extra());

        return acudiente;
    }

    /**
     * Actualiza una entidad Acudiente existente con los datos del request.
     * Útil para operaciones de actualización (PUT/PATCH).
     *
     * @param acudiente Entidad existente a actualizar
     * @param request DTO con los nuevos datos
     */
    public static void updateEntity(Acudiente acudiente, AcudienteRequest request) {
        if (acudiente == null || request == null) {
            return;
        }

        // Actualizar campos heredados de Usuario
        if (request.getNombre() != null) {
            acudiente.setNombre(request.getNombre());
        }
        if (request.getApellido() != null) {
            acudiente.setApellido(request.getApellido());
        }
        if (request.getCedula() != null) {
            acudiente.setCedula(request.getCedula());
        }
        if (request.getCorreo() != null) {
            acudiente.setCorreo(request.getCorreo());
        }

        // Actualizar campos propios de Acudiente
        if (request.getContacto_extra() != null) {
            acudiente.setContacto_extra(request.getContacto_extra());
        }
    }
}
