package cz.spookelsesfly.invoice_maker.model.exception;

public class ClientValidationException extends RuntimeException {
    public ClientValidationException(String message) {
        super(message);
    }
}
