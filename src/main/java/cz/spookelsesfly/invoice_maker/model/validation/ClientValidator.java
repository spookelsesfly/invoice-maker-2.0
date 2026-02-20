package cz.spookelsesfly.invoice_maker.model.validation;

import cz.spookelsesfly.invoice_maker.model.entity.Client;
import cz.spookelsesfly.invoice_maker.model.exception.ClientValidationException;
import org.springframework.stereotype.Component;

@Component
public class ClientValidator {

    public void validateClient(Client client) {
        if (client == null) {
            throw new ClientValidationException("Client is null.");
        }

        validateRequired(client.getFirstName(), "First name is required.");
        validateRequired(client.getLastName(), "Last name is required.");
        validateRequired(client.getPhone(), "Phone number is required.");
        validateRequired(client.getEmail(), "Email is required.");
        validateEmail(client.getEmail());
        validateAddress(
                client.getAddressFirstLine(),
                client.getAddressSecondLine(),
                client.getAddressState()
        );
    }

    public void validateRequired(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new ClientValidationException(message);
        }
    }

    public void validateEmail(String email) {
        if (!email.contains("@")) {
            throw new ClientValidationException("Email format is not valid.");
        }
    }

    public void validateAddress(String firstLine, String secondLine, String state) {
        if (firstLine == null || firstLine.trim().isEmpty() ||
                secondLine == null || secondLine.trim().isEmpty() ||
                state == null || state.trim().isEmpty()) {
            throw new ClientValidationException("Complete all address fields.");
        }
    }
}
