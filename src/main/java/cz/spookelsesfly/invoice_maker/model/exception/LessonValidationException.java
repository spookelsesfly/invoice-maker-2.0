package cz.spookelsesfly.invoice_maker.model.exception;

public class LessonValidationException extends RuntimeException {
    public LessonValidationException(String message) {
        super(message);
    }
}
