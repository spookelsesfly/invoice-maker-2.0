package cz.spookelsesfly.invoice_maker.model.service;

import cz.spookelsesfly.invoice_maker.model.entity.Client;
import cz.spookelsesfly.invoice_maker.model.exception.ClientValidationException;
import cz.spookelsesfly.invoice_maker.model.repository.ClientRepository;
import cz.spookelsesfly.invoice_maker.model.validation.ClientValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientValidator clientValidator;

    public ClientService(ClientRepository clientRepository, ClientValidator clientValidator) {
        this.clientRepository = clientRepository;
        this.clientValidator = clientValidator;
    }

    public void addNewClient(Client client) {
        clientValidator.validateClient(client);
        ifAlreadyExist(client);
        clientRepository.persist(client);
    }

    public void updateClient(Client client) {
        clientValidator.validateClient(client);
        clientRepository.merge(client);
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    private void ifAlreadyExist(Client newClient) {
       if (clientRepository.findByFirstNameAndLastName(newClient.getFirstName(), newClient.getLastName()).isPresent()) {
            throw new ClientValidationException("Client with this name already exist in database.");
        }
    }
}
