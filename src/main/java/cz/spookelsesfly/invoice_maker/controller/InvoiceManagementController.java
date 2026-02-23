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
    private final CommonInterface commonInterface;

    public InvoiceManagementController(InvoiceService invoiceService,
                                       ClientService clientService,
                                       LessonService lessonService,
                                       CommonInterface commonInterface) {
        this.invoiceService = invoiceService;
        this.clientService = clientService;
        this.lessonService = lessonService;
        this.commonInterface = commonInterface;
    }

    @FXML
    private void initialize() {
        commonInterface.setupErrorLabel(invoicesToUpdateErrorLabel);
        commonInterface.setupErrorLabel(clientsForInvoiceErrorLabel);
        commonInterface.setupErrorLabel(lessonTypeErrorLabel);
        commonInterface.setupErrorLabel(lessonsAmountErrorLabel);
        commonInterface.setupErrorLabel(creationDateErrorLabel);
        commonInterface.setupErrorLabel(paymentDateErrorLabel);
        commonInterface.setupErrorLabel(warningLabel);

        invoiceNumberLabel.setText("Invoice number");

        commonInterface.reloadComboBox(invoicesToUpdateComboBox, invoiceService::findAll);
        commonInterface.reloadComboBox(clientsForInvoiceComboBox, clientService::findAll);
        commonInterface.reloadComboBox(lessonTypeComboBox, lessonService::findAll);

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
        commonInterface.clearErrorLabel(paymentDateErrorLabel);
    }

    @FXML
    private void update() {
        clearErrors();

        boolean valid = true;

        valid &= commonInterface.validateComboBoxSelected(
                invoicesToUpdateComboBox,
                invoicesToUpdateErrorLabel,
                "Choose invoice to update."
        );

        valid &= commonInterface.validateComboBoxSelected(
                clientsForInvoiceComboBox,
                clientsForInvoiceErrorLabel,
                "Client is required."
        );

        valid &= commonInterface.validateComboBoxSelected(
                lessonTypeComboBox,
                lessonTypeErrorLabel,
                "Lesson type is required."
        );

        valid &= commonInterface.validatePositive(
                lessonsAmount.getText(),
                lessonsAmountErrorLabel,
                "Amount must be a positive number."
        );

        LocalDate created = creationDatePicker.getValue();
        if (created == null) {
            commonInterface.showErrorLabel(creationDateErrorLabel, "Date of creation is required.");
            valid = false;
        } else {
            commonInterface.clearErrorLabel(creationDateErrorLabel);
        }

        LocalDate paidDate = paymentDatePicker.getValue();
        if (paidDate != null && created != null && paidDate.isBefore(created)) {
            commonInterface.showErrorLabel(paymentDateErrorLabel, "Payment date cannot be before creation date.");
            valid = false;
        } else {
            commonInterface.clearErrorLabel(paymentDateErrorLabel);
        }

        if (!valid) {
            commonInterface.showErrorLabel(warningLabel, "Please fix highlighted fields.");
            return;
        }

        int amount = Integer.parseInt(lessonsAmount.getText().trim());

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
            commonInterface.showErrorLabel(warningLabel, e.getMessage());
            return;

        } catch (Exception e) {
            commonInterface.showErrorLabel(warningLabel, e.getMessage() == null ? "Unexpected error occurred." : e.getMessage());
            return;
        }

        commonInterface.reloadComboBox(invoicesToUpdateComboBox, invoiceService::findAll);
        invoicesToUpdateComboBox.getSelectionModel().clearSelection();
        clearForm();
        invoiceNumberLabel.setText("Invoice number");
        updateButton.setText("Update");
    }

    private void clearForm() {
        clientsForInvoiceComboBox.getSelectionModel().clearSelection();
        lessonTypeComboBox.getSelectionModel().clearSelection();
        lessonsAmount.clear();
        creationDatePicker.setValue(null);
        paymentDatePicker.setValue(null);
    }

    private void clearErrors() {
        commonInterface.clearErrorLabel(invoicesToUpdateErrorLabel);
        commonInterface.clearErrorLabel(clientsForInvoiceErrorLabel);
        commonInterface.clearErrorLabel(lessonTypeErrorLabel);
        commonInterface.clearErrorLabel(lessonsAmountErrorLabel);
        commonInterface.clearErrorLabel(creationDateErrorLabel);
        commonInterface.clearErrorLabel(paymentDateErrorLabel);
        commonInterface.clearErrorLabel(warningLabel);
    }
}