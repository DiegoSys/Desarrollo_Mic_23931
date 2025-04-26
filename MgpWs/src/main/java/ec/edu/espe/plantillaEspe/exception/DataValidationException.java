package ec.edu.espe.plantillaEspe.exception;

/**
 * Excepción lanzada cuando falla la validación de datos
 */
public class DataValidationException extends RuntimeException {

    public DataValidationException(String message) {
        super(message);
    }

    public DataValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}