package cz.spookelsesfly.invoice_maker.model.service;

import cz.spookelsesfly.invoice_maker.model.entity.Client;
import cz.spookelsesfly.invoice_maker.model.exception.ClientValidationException;
import cz.spookelsesfly.invoice_maker.model.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void addNewClient(Client client) {
        validateClient(client);
        validateIfAlreadyExist(client);
        clientRepository.persist(client);
    }

    public void updateClient(Client client) {
        validateClient(client);
        clientRepository.merge(client);
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    private void validateClient(Client client) {
        if (client == null) {
            throw new ClientValidationException("Client is null.");
        }

        validateRequired(client.getFirstName(), "First name is required.");
        validateRequired(client.getLastName(), "Last name is required.");
        validateRequired(client.getPhone(), "Phone number is required.");
        validateRequired(client.getEmail(), "Email is required.");
        validateEmailFormat(client.getEmail());
        validateAddress(
                client.getAddressFirstLine(),
                client.getAddressSecondLine(),
                client.getAddressState()
        );
    }

    private void validateRequired(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new ClientValidationException(message);
        }
    }

    private void validateEmailFormat(String email) {
        if (!email.contains("@")) {
            throw new ClientValidationException("Email format is not valid.");
        }
    }

    private void validateAddress(String firstLine, String secondLine, String state) {
        if (firstLine == null || firstLine.trim().isEmpty() ||
                secondLine == null || secondLine.trim().isEmpty() ||
                state == null || state.trim().isEmpty()) {
            throw new ClientValidationException("Complete all address fields.");
        }
    }

    private void validateIfAlreadyExist(Client newClient) {
       if (clientRepository.findByFirstNameAndLastName(newClient.getFirstName(), newClient.getLastName()).isPresent()) {
            throw new ClientValidationException("Client with this name already exist in database.");
        }
    }
}
