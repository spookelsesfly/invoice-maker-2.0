package cz.spookelsesfly.invoice_maker.model.validation;

import cz.spookelsesfly.invoice_maker.model.entity.Invoice;
import cz.spookelsesfly.invoice_maker.model.exception.InvoiceValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class InvoiceValidator {

    public void validateInvoice(Invoice invoice) {
        requireNotNull(invoice, "Invoice is null.");

        requireNotNull(invoice.getClient(), "Client is required.");
        requireNotNull(invoice.getLesson(), "Lesson is required.");

        validatePositive(invoice.getLessonAmount(), "Lessons amount must be greater than 0.");

        LocalDate created = requireNotNull(invoice.getDate(), "Date of creation is required.");

        validatePositive(invoice.getValue(), "Invoice value must be greater than 0.");

        if (invoice.isPayed()) {
            LocalDate paid = requireNotNull(invoice.getDateOfPayment(), "Date of payment is required for paid invoice.");
            validateDateOfPayment(created, paid);
        } else {
             if (invoice.getDateOfPayment() != null) {
                 throw new InvoiceValidationException("Unpaid invoice must not have payment date.");
             }
        }
    }

    public void validateDateOfPayment(LocalDate dateOfCreation, LocalDate dateOfPayment) {
        requireNotNull(dateOfCreation, "Date of creation is required.");
        requireNotNull(dateOfPayment, "Date of payment is required.");

        if (dateOfPayment.isBefore(dateOfCreation)) {
            throw new InvoiceValidationException("Payment date cannot be before creation date.");
        }
    }

    public <T> T requireNotNull(T value, String message) {
        if (value == null) {
            throw new InvoiceValidationException(message);
        }
        return value;
    }

    public void validatePositive(int value, String message) {
        if (value < 1) {
            throw new InvoiceValidationException(message);
        }
    }
}