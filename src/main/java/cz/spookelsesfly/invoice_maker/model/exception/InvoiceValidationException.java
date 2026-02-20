package cz.spookelsesfly.invoice_maker.model.exception;

public class InvoiceValidationException extends RuntimeException {
    public InvoiceValidationException(String message) {
        super(message);
    }
}
