package cz.spookelsesfly.invoice_maker.controller;

import cz.spookelsesfly.invoice_maker.model.entity.Client;
import cz.spookelsesfly.invoice_maker.model.entity.Invoice;
import cz.spookelsesfly.invoice_maker.model.entity.Lesson;
import cz.spookelsesfly.invoice_maker.model.exception.InvoiceValidationException;
import cz.spookelsesfly.invoice_maker.model.service.ClientService;
import cz.spookelsesfly.invoice_maker.model.service.InvoiceService;
import cz.spookelsesfly.invoice_maker.model.service.LessonService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class InvoiceManagementController {

    @FXML
    private ComboBox<Invoice> invoicesToUpdateComboBox;

    @FXML
    private Label invoicesToUpdateErrorLabel;

    @FXML
    private Label invoiceNumberLabel;

    @FXML
    private ComboBox<Client> clientsForInvoiceComboBox;

    @FXML
    private Label clientsForInvoiceErrorLabel;

    @FXML
    private ComboBox<Lesson> lessonTypeComboBox;

    @FXML
    private Label lessonTypeErrorLabel;

    @FXML
    private TextField lessonsAmount;

    @FXML
    private Label lessonsAmountErrorLabel;

    @FXML
    private DatePicker creationDatePicker;

    @FXML
    private Label creationDateErrorLabel;

    @FXML
    private DatePicker paymentDatePicker;

    @FXML
    private Label paymentDateErrorLabel;

    @FXML
    private Button updateButton;

    @FXML
    private Label warningLabel;

    private final InvoiceService invoiceService;
    private final ClientService clientService;
    private final LessonService lessonService;

    public InvoiceManagementController(InvoiceService invoiceService,
                                       ClientService clientService,
                                       LessonService lessonService) {
        this.invoiceService = invoiceService;
        this.clientService = clientService;
        this.lessonService = lessonService;
    }

    @FXML
    private void initialize() {
        setupErrorLabel(invoicesToUpdateErrorLabel);
        setupErrorLabel(clientsForInvoiceErrorLabel);
        setupErrorLabel(lessonTypeErrorLabel);
        setupErrorLabel(lessonsAmountErrorLabel);
        setupErrorLabel(creationDateErrorLabel);
        setupErrorLabel(paymentDateErrorLabel);
        setupErrorLabel(warningLabel);

        invoiceNumberLabel.setText("Invoice number");

        reloadInvoicesComboBox();
        reloadClientsComboBox();
        reloadLessonsComboBox();

        invoicesToUpdateComboBox.getSelectionModel().clearSelection();
        clearForm();
        clearErrors();
    }

    @FXML
    public void fillUpdateForm() {
        clearErrors();
        warningLabel.setVisible(false);

        Invoice invoice = invoicesToUpdateComboBox.getValue();
        if (invoice == null) {
            invoiceNumberLabel.setText("Invoice number");
            clearForm();
            updateButton.setText("Update");
            return;
        }

        invoiceNumberLabel.setText("Invoice number " + invoice.getNumber());

        clientsForInvoiceComboBox.setValue(invoice.getClient());
        lessonTypeComboBox.setValue(invoice.getLesson());
        lessonsAmount.setText(String.valueOf(invoice.getLessonAmount()));
        creationDatePicker.setValue(invoice.getDate());

        paymentDatePicker.setValue(invoice.isPayed() ? invoice.getDateOfPayment() : null);

        updateButton.setText("Update " + invoice.getNumber());
    }

    @FXML
    private void markAsNotPaid() {
        Invoice invoice = invoicesToUpdateComboBox.getValue();
        if (invoice != null) {
            invoice.setPayed(false);
            invoice.setDateOfPayment(null);
        }
        paymentDatePicker.setValue(null);
        clearErrorLabel(paymentDateErrorLabel);
    }

    @FXML
    private void update() {
        clearErrors();

        boolean valid = true;

        valid &= validateComboBoxSelected(invoicesToUpdateComboBox, invoicesToUpdateErrorLabel, "Choose invoice to update.");
        valid &= validateComboBoxSelected(clientsForInvoiceComboBox, clientsForInvoiceErrorLabel, "Client is required.");
        valid &= validateComboBoxSelected(lessonTypeComboBox, lessonTypeErrorLabel, "Lesson type is required.");

        Integer amount = parsePositiveIntOrNull(lessonsAmount.getText());
        if (amount == null) {
            lessonsAmountErrorLabel.setText("Amount must be a positive number.");
            lessonsAmountErrorLabel.setVisible(true);
            valid = false;
        } else {
            clearErrorLabel(lessonsAmountErrorLabel);
        }

        LocalDate created = creationDatePicker.getValue();
        if (created == null) {
            creationDateErrorLabel.setText("Date of creation is required.");
            creationDateErrorLabel.setVisible(true);
            valid = false;
        } else {
            clearErrorLabel(creationDateErrorLabel);
        }

        LocalDate paidDate = paymentDatePicker.getValue();
        if (paidDate != null && created != null && paidDate.isBefore(created)) {
            paymentDateErrorLabel.setText("Payment date cannot be before creation date.");
            paymentDateErrorLabel.setVisible(true);
            valid = false;
        } else {
            clearErrorLabel(paymentDateErrorLabel);
        }

        if (!valid) {
            warningLabel.setText("Please fix highlighted fields.");
            warningLabel.setVisible(true);
            return;
        }

        Invoice invoice = invoicesToUpdateComboBox.getValue();
        Client client = clientsForInvoiceComboBox.getValue();
        Lesson lesson = lessonTypeComboBox.getValue();

        invoice.setClient(client);
        invoice.setLesson(lesson);
        invoice.setLessonAmount(amount);
        invoice.setDate(created);

        if (paidDate == null) {
            invoice.setPayed(false);
            invoice.setDateOfPayment(null);
        } else {
            invoice.setPayed(true);
            invoice.setDateOfPayment(paidDate);
        }

        invoice.setValue(lesson.getPrice() * amount);

        try {
            invoiceService.updateInvoice(invoice);

        } catch (InvoiceValidationException e) {
            warningLabel.setText(e.getMessage());
            warningLabel.setVisible(true);
            return;

        } catch (Exception e) {
            warningLabel.setText(e.getMessage() == null ? "Unexpected error occurred." : e.getMessage());
            warningLabel.setVisible(true);
            return;
        }

        reloadInvoicesComboBox();
        invoicesToUpdateComboBox.getSelectionModel().clearSelection();
        clearForm();
        invoiceNumberLabel.setText("Invoice number");
        updateButton.setText("Update");
    }

    private <T> boolean validateComboBoxSelected(ComboBox<T> comboBox, Label errorLabel, String message) {
        if (comboBox.getValue() == null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            return false;
        }
        clearErrorLabel(errorLabel);
        return true;
    }

    private Integer parsePositiveIntOrNull(String s) {
        try {
            int n = Integer.parseInt(s.trim());
            return n > 0 ? n : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void clearForm() {
        clientsForInvoiceComboBox.getSelectionModel().clearSelection();
        lessonTypeComboBox.getSelectionModel().clearSelection();
        lessonsAmount.clear();
        creationDatePicker.setValue(null);
        paymentDatePicker.setValue(null);
    }

    private void clearErrors() {
        clearErrorLabel(invoicesToUpdateErrorLabel);
        clearErrorLabel(clientsForInvoiceErrorLabel);
        clearErrorLabel(lessonTypeErrorLabel);
        clearErrorLabel(lessonsAmountErrorLabel);
        clearErrorLabel(creationDateErrorLabel);
        clearErrorLabel(paymentDateErrorLabel);
        clearErrorLabel(warningLabel);
    }

    private void setupErrorLabel(Label label) {
        label.setVisible(false);
        label.managedProperty().bind(label.visibleProperty());
    }

    private void clearErrorLabel(Label label) {
        label.setText("");
        label.setVisible(false);
    }

    private void reloadInvoicesComboBox() {
        invoicesToUpdateComboBox.getItems().setAll(invoiceService.findAll());
    }

    private void reloadClientsComboBox() {
        clientsForInvoiceComboBox.getItems().setAll(clientService.findAll());
    }

    private void reloadLessonsComboBox() {
        lessonTypeComboBox.getItems().setAll(lessonService.findAll());
    }
}