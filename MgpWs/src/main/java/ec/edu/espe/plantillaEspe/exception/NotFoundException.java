package ec.edu.espe.plantillaEspe.exception;

/**
 * Excepción lanzada cuando no se encuentra un recurso solicitado
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}