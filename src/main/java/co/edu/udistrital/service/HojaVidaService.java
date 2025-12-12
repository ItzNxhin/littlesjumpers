package co.edu.udistrital.service;

import co.edu.udistrital.dto.HojaVidaRequest;
import co.edu.udistrital.dto.HojaVidaResponse;
import co.edu.udistrital.model.Estudiante;
import co.edu.udistrital.model.HojaVida;
import co.edu.udistrital.repository.EstudianteRepository;
import co.edu.udistrital.repository.HojaVidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class HojaVidaService {

    @Autowired
    private HojaVidaRepository hojaVidaRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    /**
     * Obtiene la hoja de vida de un estudiante.
     * Si no existe, la crea automáticamente.
     *
     * @param estudianteId ID del estudiante
     * @return HojaVidaResponse con los datos de la hoja de vida
     */
    @Transactional
    public HojaVidaResponse obtenerOCrearPorEstudiante(Integer estudianteId) {
        try {
            Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

            // Buscar hoja de vida existente
            Optional<HojaVida> hojaVidaOpt = hojaVidaRepository.findByEstudianteId(estudianteId);

            HojaVida hojaVida;
            if (hojaVidaOpt.isEmpty()) {
                // Crear nueva HojaVida
                hojaVida = new HojaVida(estudiante);
                hojaVida.setAlergias("");
                hojaVida.setNotasAprendizaje("");
                hojaVida.setEstadoSalud("");
                hojaVida = hojaVidaRepository.save(hojaVida);
            } else {
                hojaVida = hojaVidaOpt.get();
            }

            return new HojaVidaResponse(hojaVida);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error al consultar hoja de vida", e);
        }
    }

    /**
     * Obtiene la hoja de vida de un estudiante (sobrecarga que recibe la entidad).
     * Si no existe, la crea automáticamente.
     */
    @Transactional
    public HojaVidaResponse obtenerOCrearPorEstudiante(Estudiante estudiante) {
        return obtenerOCrearPorEstudiante(estudiante.getId());
    }

    /**
     * Actualiza la hoja de vida de un estudiante
     *
     * @param estudianteId ID del estudiante
     * @param request Datos a actualizar
     * @return HojaVidaResponse con los datos actualizados
     */
    @Transactional
    public HojaVidaResponse actualizar(Integer estudianteId, HojaVidaRequest request) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        return actualizar(estudiante, request);
    }

    /**
     * Actualiza la hoja de vida de un estudiante (sobrecarga que recibe la entidad)
     *
     * @param estudiante Entidad estudiante
     * @param request Datos a actualizar
     * @return HojaVidaResponse con los datos actualizados
     */
    @Transactional
    public HojaVidaResponse actualizar(Estudiante estudiante, HojaVidaRequest request) {
        try {
            // Obtener o crear hoja de vida
            obtenerOCrearPorEstudiante(estudiante);

            // Buscar la entidad para actualizar
            Optional<HojaVida> hojaVidaOpt = hojaVidaRepository.findByEstudianteId(estudiante.getId());

            if (hojaVidaOpt.isEmpty()) {
                throw new RuntimeException("Error al actualizar hoja de vida");
            }

            HojaVida hojaVida = hojaVidaOpt.get();

            // Actualizar campos
            hojaVida.setEstadoSalud(request.getEstadoSalud());
            hojaVida.setAlergias(request.getAlergias());
            hojaVida.setNotasAprendizaje(request.getNotasAprendizaje());

            HojaVida actualizada = hojaVidaRepository.save(hojaVida);
            return new HojaVidaResponse(actualizada);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al actualizar hoja de vida", e);
        }
    }

    /**
     * Elimina la hoja de vida de un estudiante
     *
     * @param estudianteId ID del estudiante
     * @return true si se eliminó, false si no existía
     */
    @Transactional
    public boolean eliminar(Integer estudianteId) {
        try {
            Optional<HojaVida> hojaVidaOpt = hojaVidaRepository.findByEstudianteId(estudianteId);

            if (hojaVidaOpt.isEmpty()) {
                return false;
            }

            hojaVidaRepository.delete(hojaVidaOpt.get());
            return true;
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al eliminar hoja de vida", e);
        }
    }
}
