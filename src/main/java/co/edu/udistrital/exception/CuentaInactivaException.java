package co.edu.udistrital.exception;

/**
 * Indica que las credenciales pueden ser correctas, pero la cuenta está desactivada.
 *
 * Es diferente a unas credenciales inválidas: el usuario existe, pero no tiene
 * permitido acceder mientras su cuenta permanezca inactiva.
 */
public class CuentaInactivaException extends AutenticacionException {

    /**
     * Crea la excepción con el mensaje que se mostrará al cliente.
     */
    public CuentaInactivaException(String message) {
        super(message);
    }
}
