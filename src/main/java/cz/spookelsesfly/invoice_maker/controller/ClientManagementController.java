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

    public ClientManagementController(ClientService clientService) {
        this.clientService = clientService;
    }

    @FXML
    private void initialize() {
        setupErrorLabel(warningLabel);

        setupErrorLabel(firstNameErrorLabel);
        setupErrorLabel(lastNameErrorLabel);
        setupErrorLabel(phoneErrorLabel);
        setupErrorLabel(emailErrorLabel);
        setupErrorLabel(addressErrorLabel);

        addNewClient();
        reloadClientsComboBox();
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

        valid &= validateRequired(firstNameField.getText(), firstNameErrorLabel, "First name is required.");
        valid &= validateRequired(lastNameField.getText(), lastNameErrorLabel, "Last name is required.");
        valid &= validateRequired(phoneField.getText(), phoneErrorLabel, "Phone number is required.");

        boolean emailRequired = validateRequired(emailField.getText(), emailErrorLabel, "Email is required.");
        valid &= emailRequired;
        if (emailRequired) {
            valid &= validateEmailFormat(emailField.getText(), emailErrorLabel);
        }

        valid &= validateAddress(
                addressFirstLineField.getText(),
                addressSecondLineField.getText(),
                addressStateField.getText(),
                addressErrorLabel
        );

        if (!valid) {
            warningLabel.setText("Please fix highlighted fields.");
            warningLabel.setVisible(true);
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
            warningLabel.setText(e.getMessage());
            warningLabel.setVisible(true);
            return;
        }

        clearForm();
        reloadClientsComboBox();
        clientsToUpdateComboBox.getSelectionModel().clearSelection();
    }

    private boolean isUpdate() {
        return !clientsToUpdateComboBox.getSelectionModel().isEmpty();
    }

    private void fillForm(Client client) {
        firstNameField.setText(fillSingleField(client.getFirstName()));
        lastNameField.setText(fillSingleField(client.getLastName()));
        emailField.setText(fillSingleField(client.getEmail()));
        phoneField.setText(fillSingleField(client.getPhone()));
        addressFirstLineField.setText(fillSingleField(client.getAddressFirstLine()));
        addressSecondLineField.setText(fillSingleField(client.getAddressSecondLine()));
        addressStateField.setText(fillSingleField(client.getAddressState()));
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
        clearErrorLabel(firstNameErrorLabel);
        clearErrorLabel(lastNameErrorLabel);
        clearErrorLabel(emailErrorLabel);
        clearErrorLabel(phoneErrorLabel);
        clearErrorLabel(addressErrorLabel);
        clearErrorLabel(warningLabel);
    }

    private boolean validateRequired(String value, Label errorLabel, String message) {
        if (value == null || value.trim().isEmpty()) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            return false;
        }
        clearErrorLabel(errorLabel);
        return true;
    }

    private boolean validateEmailFormat(String email, Label errorLabel) {
        if (!email.contains("@")) {
            errorLabel.setText("Email format is not valid.");
            errorLabel.setVisible(true);
            return false;
        }
        clearErrorLabel(errorLabel);
        return true;
    }

    private boolean validateAddress(String firstLine, String secondLine, String state, Label errorLabel) {
        if (firstLine == null || firstLine.trim().isEmpty() ||
            secondLine == null || secondLine.trim().isEmpty() ||
            state == null || state.trim().isEmpty()) {
            errorLabel.setText("Complete all address fields.");
            errorLabel.setVisible(true);
            return false;
        }
        clearErrorLabel(errorLabel);
        return true;
    }

    private void setupErrorLabel(Label label) {
        label.setVisible(false);
        label.managedProperty().bind(label.visibleProperty());
    }

    private void clearErrorLabel(Label label) {
        label.setText("");
        label.setVisible(false);
    }

    private void reloadClientsComboBox() {
        clientsToUpdateComboBox.getItems().setAll(clientService.findAll());
    }

    private String fillSingleField(String s) {
        return s == null ? "" : s;
    }
}
