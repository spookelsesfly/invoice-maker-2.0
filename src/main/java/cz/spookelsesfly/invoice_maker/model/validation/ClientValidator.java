package cz.spookelsesfly.invoice_maker.model.validation;

import cz.spookelsesfly.invoice_maker.model.entity.Client;
import cz.spookelsesfly.invoice_maker.model.exception.ClientValidationException;
import org.springframework.stereotype.Component;

@Component
public class ClientValidator {

    private final CommonValidator commonValidator;

    public ClientValidator(CommonValidator commonValidator) {
        this.commonValidator = commonValidator;
    }

    public void validateClient(Client client) {
        commonValidator.requireNotNull(client, () -> new ClientValidationException("Client is null."));

        commonValidator.requireRequired(client.getFirstName(),
                () -> new ClientValidationException("First name is required."));
        commonValidator.requireRequired(client.getLastName(),
                () -> new ClientValidationException("Last name is required."));
        commonValidator.requireRequired(client.getPhone(),
                () -> new ClientValidationException("Phone number is required."));

        commonValidator.requireRequired(client.getEmail(),
                () -> new ClientValidationException("Email is required."));
        commonValidator.requireEmailLike(client.getEmail(),
                () -> new ClientValidationException("Email format is not valid."));

        commonValidator.requireCompleteAddress(
                client.getAddressFirstLine(),
                client.getAddressSecondLine(),
                client.getAddressState(),
                () -> new ClientValidationException("Complete all address fields.")
        );
    }
}