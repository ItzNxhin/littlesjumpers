package co.edu.udistrital.exception;

public class AutenticacionException extends RuntimeException {

    public AutenticacionException(String message) {
        super(message);
    }

    public AutenticacionException(String message, Throwable cause) {
        super(message, cause);
    }
}
