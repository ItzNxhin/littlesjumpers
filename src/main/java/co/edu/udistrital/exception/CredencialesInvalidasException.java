package co.edu.udistrital.exception;

/**
 * Indica que el usuario o la contraseña proporcionados no son válidos.
 *
 * Es un tipo específico de AutenticacionException para que el manejador global
 * pueda devolver una respuesta HTTP 401 (no autorizado).
 */
public class CredencialesInvalidasException extends AutenticacionException {

    /**
     * Crea la excepción con el mensaje que se mostrará al cliente.
     */
    public CredencialesInvalidasException(String message) {
        super(message);
    }
}
