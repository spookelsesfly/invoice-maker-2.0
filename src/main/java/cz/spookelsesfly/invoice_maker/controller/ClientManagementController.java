package cz.spookelsesfly.invoice_maker.controller;

import cz.spookelsesfly.invoice_maker.model.entity.Client;
import cz.spookelsesfly.invoice_maker.model.exception.ClientValidationException;
import cz.spookelsesfly.invoice_maker.model.service.ClientService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

@Component
public class ClientManagementController {

    @FXML
    private TextField firstNameField;

    @FXML
    private Label firstNameErrorLabel;

    @FXML
    private TextField lastNameField;

    @FXML
    private Label lastNameErrorLabel;

    @FXML
    private TextField phoneField;

    @FXML
    private Label phoneErrorLabel;

    @FXML
    private TextField emailField;

    @FXML
    private Label emailErrorLabel;

    @FXML
    private TextField addressFirstLineField;

    @FXML
    private TextField addressSecondLineField;

    @FXML
    private TextField addressStateField;

    @FXML
    private Label addressErrorLabel;

    @FXML
    private Label warningLabel;

    @FXML
    private ComboBox<Client> clientsToUpdateComboBox;

    @FXML
    private Button updateButton;

    private final ClientService clientService;
    private final CommonInterface commonInterface;

    public ClientManagementController(ClientService clientService, CommonInterface commonInterface) {
        this.clientService = clientService;
        this.commonInterface = commonInterface;
    }

    @FXML
    private void initialize() {
        commonInterface.setupErrorLabel(warningLabel);

        commonInterface.setupErrorLabel(firstNameErrorLabel);
        commonInterface.setupErrorLabel(lastNameErrorLabel);
        commonInterface.setupErrorLabel(phoneErrorLabel);
        commonInterface.setupErrorLabel(emailErrorLabel);
        commonInterface.setupErrorLabel(addressErrorLabel);

        addNewClient();
        commonInterface.reloadComboBox(clientsToUpdateComboBox, clientService::findAll);
    }

    @FXML
    private void addNewClient() {
        clientsToUpdateComboBox.getSelectionModel().clearSelection();
        clearForm();
        clearErrors();
        warningLabel.setVisible(false);
        updateButton.setText("Add new client");
    }

    @FXML
    public void fillUpdateForm() {
        clearErrors();
        warningLabel.setVisible(false);

        Client client = clientsToUpdateComboBox.getValue();
        if (client == null) {
            clearForm();
            updateButton.setText("Add new client");
            return;
        }

        fillForm(client);
        updateButton.setText("Update " + client);
    }

    @FXML
    private void update() {
        clearErrors();

        boolean valid = true;

        valid &= commonInterface.validateRequired(firstNameField.getText(), firstNameErrorLabel, "First name is required.");
        valid &= commonInterface.validateRequired(lastNameField.getText(), lastNameErrorLabel, "Last name is required.");
        valid &= commonInterface.validateRequired(phoneField.getText(), phoneErrorLabel, "Phone number is required.");

        boolean emailRequired = commonInterface.validateRequired(emailField.getText(), emailErrorLabel, "Email is required.");
        valid &= emailRequired;
        if (emailRequired) {
            valid &= validateEmail(emailField.getText(), emailErrorLabel);
        }

        valid &= validateAddress(
                addressFirstLineField.getText(),
                addressSecondLineField.getText(),
                addressStateField.getText(),
                addressErrorLabel
        );

        if (!valid) {
            commonInterface.showErrorLabel(warningLabel, "Please fix highlighted fields.");
            return;
        }

        Client client = isUpdate() ? clientsToUpdateComboBox.getValue() : new Client();

        client.setFirstName(firstNameField.getText());
        client.setLastName(lastNameField.getText());
        client.setPhone(phoneField.getText());
        client.setEmail(emailField.getText());
        client.setAddressFirstLine(addressFirstLineField.getText());
        client.setAddressSecondLine(addressSecondLineField.getText());
        client.setAddressState(addressStateField.getText());

        try {
            if (isUpdate()) {
                clientService.updateClient(client);
            } else {
                clientService.addNewClient(client);
            }
        } catch (ClientValidationException e) {
            commonInterface.showErrorLabel(warningLabel, e.getMessage());
            return;
        }

        clearForm();
        commonInterface.reloadComboBox(clientsToUpdateComboBox, clientService::findAll);
        clientsToUpdateComboBox.getSelectionModel().clearSelection();
    }

    private boolean isUpdate() {
        return !clientsToUpdateComboBox.getSelectionModel().isEmpty();
    }

    private void fillForm(Client client) {
        firstNameField.setText(commonInterface.fillSingleField(client.getFirstName()));
        lastNameField.setText(commonInterface.fillSingleField(client.getLastName()));
        emailField.setText(commonInterface.fillSingleField(client.getEmail()));
        phoneField.setText(commonInterface.fillSingleField(client.getPhone()));
        addressFirstLineField.setText(commonInterface.fillSingleField(client.getAddressFirstLine()));
        addressSecondLineField.setText(commonInterface.fillSingleField(client.getAddressSecondLine()));
        addressStateField.setText(commonInterface.fillSingleField(client.getAddressState()));
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        phoneField.clear();
        emailField.clear();
        addressFirstLineField.clear();
        addressSecondLineField.clear();
        addressStateField.clear();
    }

    private void clearErrors() {
        commonInterface.clearErrorLabel(firstNameErrorLabel);
        commonInterface.clearErrorLabel(lastNameErrorLabel);
        commonInterface.clearErrorLabel(emailErrorLabel);
        commonInterface.clearErrorLabel(phoneErrorLabel);
        commonInterface.clearErrorLabel(addressErrorLabel);
        commonInterface.clearErrorLabel(warningLabel);
    }

    private boolean validateEmail(String email, Label errorLabel) {
        if (!email.contains("@")) {
            commonInterface.showErrorLabel(errorLabel, "Email format is not valid.");
            return false;
        }
        commonInterface.clearErrorLabel(errorLabel);
        return true;
    }

    private boolean validateAddress(String firstLine, String secondLine, String state, Label errorLabel) {
        if (firstLine == null || firstLine.trim().isEmpty() ||
                secondLine == null || secondLine.trim().isEmpty() ||
                state == null || state.trim().isEmpty()) {
            commonInterface.showErrorLabel(errorLabel, "Complete all address fields.");
            return false;
        }
        commonInterface.clearErrorLabel(errorLabel);
        return true;
    }
}