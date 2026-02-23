package cz.spookelsesfly.invoice_maker.model.validation;

import cz.spookelsesfly.invoice_maker.model.entity.Invoice;
import cz.spookelsesfly.invoice_maker.model.exception.InvoiceValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class InvoiceValidator {

    private final CommonValidator commonValidator;

    public InvoiceValidator(CommonValidator commonValidator) {
        this.commonValidator = commonValidator;
    }

    public void validateInvoice(Invoice invoice) {
        commonValidator.requireNotNull(invoice, () -> new InvoiceValidationException("Invoice is null."));

        commonValidator.requireNotNull(invoice.getClient(), () -> new InvoiceValidationException("Client is required."));
        commonValidator.requireNotNull(invoice.getLesson(), () -> new InvoiceValidationException("Lesson is required."));

        commonValidator.requirePositive(invoice.getLessonAmount(),
                () -> new InvoiceValidationException("Lessons amount must be greater than 0."));

        LocalDate created = commonValidator.requireNotNull(invoice.getDate(),
                () -> new InvoiceValidationException("Date of creation is required."));

        commonValidator.requirePositive(invoice.getValue(),
                () -> new InvoiceValidationException("Invoice value must be greater than 0."));

        if (invoice.isPayed()) {
            LocalDate paid = commonValidator.requireNotNull(invoice.getDateOfPayment(),
                    () -> new InvoiceValidationException("Date of payment is required for paid invoice."));
            validateDateOfPayment(created, paid);
        } else {
            if (invoice.getDateOfPayment() != null) {
                throw new InvoiceValidationException("Unpaid invoice must not have payment date.");
            }
        }
    }

    public void validateDateOfPayment(LocalDate dateOfCreation, LocalDate dateOfPayment) {
        commonValidator.requireNotNull(dateOfCreation,
                () -> new InvoiceValidationException("Date of creation is required."));
        commonValidator.requireNotNull(dateOfPayment,
                () -> new InvoiceValidationException("Date of payment is required."));

        if (dateOfPayment.isBefore(dateOfCreation)) {
            throw new InvoiceValidationException("Payment date cannot be before creation date.");
        }
    }
}