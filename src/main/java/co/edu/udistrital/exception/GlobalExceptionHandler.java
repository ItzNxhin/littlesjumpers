package co.edu.udistrital.exception;

import co.edu.udistrital.dto.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Intercepta excepciones lanzadas por los controladores REST y las convierte
 * en respuestas HTTP con un formato que el frontend puede procesar.
 *
 * Gracias a @RestControllerAdvice, no es necesario repetir try/catch en cada
 * controlador para los errores incluidos aquí.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Responde con 401 cuando el usuario no existe o sus credenciales son inválidas.
     */
    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<LoginResponse> handleCredencialesInvalidas(CredencialesInvalidasException ex) {
        LoginResponse response = new LoginResponse(false, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Responde con 403 cuando la cuenta existe, pero está desactivada.
     */
    @ExceptionHandler(CuentaInactivaException.class)
    public ResponseEntity<LoginResponse> handleCuentaInactiva(CuentaInactivaException ex) {
        LoginResponse response = new LoginResponse(false, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Oculta los detalles internos de la base de datos y responde con 500.
     */
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<LoginResponse> handleDatabaseException(DatabaseException ex) {
        LoginResponse response = new LoginResponse(false, "Error al acceder a la base de datos. Intente nuevamente.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Recoge errores producidos cuando los datos recibidos no cumplen las
     * validaciones declaradas en los DTO.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Último respaldo para errores no contemplados específicamente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<LoginResponse> handleGenericException(Exception ex) {
        LoginResponse response = new LoginResponse(false, "Error inesperado en el servidor. Contacte al administrador.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
