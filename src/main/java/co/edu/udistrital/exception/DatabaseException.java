package co.edu.udistrital.exception;

/**
 * Representa un error al consultar o modificar la base de datos.
 *
 * La excepción permite separar los problemas de persistencia de la lógica
 * normal del negocio y tratarlos con una respuesta común.
 */
public class DatabaseException extends RuntimeException {

    /**
     * Crea el error con un mensaje descriptivo.
     */
    public DatabaseException(String message) {
        super(message);
    }

    /**
     * Crea el error con un mensaje y conserva la excepción original como causa.
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
