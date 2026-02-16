package cz.spookelsesfly.invoice_maker.controller;

import cz.spookelsesfly.invoice_maker.model.entity.Client;
import cz.spookelsesfly.invoice_maker.model.entity.Invoice;
import cz.spookelsesfly.invoice_maker.model.entity.Lesson;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

@Component
public class SettingsController {

    // Client section
    @FXML
    private ComboBox<Client> clientBoxForClientManagement;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField secondNameField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField addressFirstLineField;

    @FXML
    private TextField addressSecondLineField;

    @FXML
    private TextField addressStateField;

    // Lesson section
    @FXML
    private ComboBox<Lesson> lessonBox;

    @FXML
    private TextField lessonTypeField;

    @FXML
    private TextField lessonDurationField;

    @FXML
    private TextField lessonPriceField;

    // Invoice section
    @FXML
    private ComboBox<Invoice> InvoiceBox;

    @FXML
    private Label invoiceNumber;

    @FXML
    private ComboBox<Client> clientBoxInvoiceForInvoiceManagement;

    @FXML
    private ComboBox<Lesson> lessonBoxInvoice;

    @FXML
    private TextField lessonAmount;

    @FXML
    private DatePicker creationDatePicker;

    @FXML
    private ToggleButton invoicePaidToggle;

    @FXML
    private DatePicker paymentDatePicker;

    @FXML
    public void initialize() {
        //TODO
    }

    @FXML
    private void addNewClient() {
        // TODO
    }

    @FXML
    private void updateClient() {
        // TODO
    }

    @FXML
    private void updateLesson() {
        // TODO
    }

    @FXML
    private void updateInvoice() {
        // TODO
    }

    @FXML
    private void invoiceTogglePaid() {
        // TODO
    }

    @FXML
    private void deleteLastInvoice() {
        // TODO
    }
}
