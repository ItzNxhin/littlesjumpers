package co.edu.udistrital.exception;

/**
 * Excepción base para los errores relacionados con el inicio de sesión.
 *
 * Al heredar de RuntimeException, no es obligatorio declarar esta excepción
 * en cada método que pueda lanzarla.
 */
public class AutenticacionException extends RuntimeException {

    /**
     * Crea un error de autenticación con un mensaje explicativo.
     */
    public AutenticacionException(String message) {
        super(message);
    }

    /**
     * Crea un error de autenticación conservando también la causa original.
     * Esto sirve para no perder información cuando un error proviene de otra excepción.
     */
    public AutenticacionException(String message, Throwable cause) {
        super(message, cause);
    }
}
