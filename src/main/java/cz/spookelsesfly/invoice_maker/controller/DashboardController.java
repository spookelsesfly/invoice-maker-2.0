package cz.spookelsesfly.invoice_maker.controller;

import cz.spookelsesfly.invoice_maker.model.entity.Client;
import cz.spookelsesfly.invoice_maker.model.entity.Invoice;
import cz.spookelsesfly.invoice_maker.model.entity.Lesson;
import cz.spookelsesfly.invoice_maker.model.service.ClientService;
import cz.spookelsesfly.invoice_maker.model.service.InvoiceService;
import cz.spookelsesfly.invoice_maker.model.service.LessonService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DashboardController {

    @FXML
    private ComboBox<Client> clientComboBox;

    @FXML
    private Label clientComboBoxErrorLabel;

    @FXML
    private ComboBox<Lesson> lessonComboBox;

    @FXML
    private Label lessonComboBoxErrorLabel;

    @FXML
    private TextField lessonsAmountField;

    @FXML
    private Label lessonsAmountErrorLabel;

    @FXML
    private Label createNewInvoiceWarningLabel;

    @FXML
    private VBox invoicesInProgressContainer;

    @FXML
    private ComboBox<Invoice> invoicesInProgressComboBox;

    @FXML
    private Label invoicesInProgressComboBoxErrorLabel;

    @FXML
    private DatePicker paymentDatePicker;

    @FXML
    private Label paymentDatePickerErrorLabel;

    @FXML
    private Label markAsPaidWarningLabel;

    private final ClientService clientService;
    private final LessonService lessonService;
    private final InvoiceService invoiceService;
    private final CommonInterface commonInterface;

    public DashboardController(ClientService clientService,
                               LessonService lessonService,
                               InvoiceService invoiceService,
                               CommonInterface commonInterface) {
        this.clientService = clientService;
        this.lessonService = lessonService;
        this.invoiceService = invoiceService;
        this.commonInterface = commonInterface;
    }

    @FXML
    private void initialize() {
        commonInterface.setupErrorLabel(clientComboBoxErrorLabel);
        commonInterface.setupErrorLabel(lessonComboBoxErrorLabel);
        commonInterface.setupErrorLabel(lessonsAmountErrorLabel);
        commonInterface.setupErrorLabel(createNewInvoiceWarningLabel);

        commonInterface.setupErrorLabel(invoicesInProgressComboBoxErrorLabel);
        commonInterface.setupErrorLabel(paymentDatePickerErrorLabel);
        commonInterface.setupErrorLabel(markAsPaidWarningLabel);

        commonInterface.reloadComboBox(clientComboBox, clientService::findAll);
        commonInterface.reloadComboBox(lessonComboBox, lessonService::findAll);
        loadInvoicesInProgress();
    }

    @FXML
    private void createNewInvoice() {
        clearCreateInvoiceErrors();

        boolean valid = true;

        valid &= commonInterface.validateComboBoxSelected(
                clientComboBox,
                clientComboBoxErrorLabel,
                "Client is required."
        );

        valid &= commonInterface.validateComboBoxSelected(
                lessonComboBox,
                lessonComboBoxErrorLabel,
                "Lesson is required."
        );

        valid &= commonInterface.validatePositive(
                lessonsAmountField.getText(),
                lessonsAmountErrorLabel,
                "Lessons amount must be greater than 0."
        );

        if (!valid) {
            commonInterface.showErrorLabel(createNewInvoiceWarningLabel, "Please fix highlighted fields.");
            return;
        }

        int amount = Integer.parseInt(lessonsAmountField.getText().trim());

        try {
            invoiceService.addNewInvoice(clientComboBox.getValue(), lessonComboBox.getValue(), amount);
            loadInvoicesInProgress();
            clearCreateInvoiceForm();

        } catch (Exception e) {
            commonInterface.showErrorLabel(createNewInvoiceWarningLabel, e.getMessage());
        }
    }

    @FXML
    private void markInvoiceAsPaid() {
        clearMarkPaidErrors();

        boolean valid = true;

        valid &= commonInterface.validateComboBoxSelected(
                invoicesInProgressComboBox,
                invoicesInProgressComboBoxErrorLabel,
                "Invoice is required.");

        if (paymentDatePicker.getValue() == null) {
            commonInterface.showErrorLabel(paymentDatePickerErrorLabel, "Payment date is required.");
            valid = false;
        } else {
            commonInterface.clearErrorLabel(paymentDatePickerErrorLabel);
        }

        if (!valid) {
            commonInterface.showErrorLabel(markAsPaidWarningLabel, "Please fix highlighted fields.");
            return;
        }

        try {
            invoiceService.markAsPaid(invoicesInProgressComboBox.getValue(), paymentDatePicker.getValue());
            loadInvoicesInProgress();
            clearMarkPaidForm();

        } catch (Exception e) {
            commonInterface.showErrorLabel(markAsPaidWarningLabel, e.getMessage());
        }
    }

    private void loadInvoicesInProgress() {
        List<Invoice> invoices = invoiceService.findAllInProcess();

        invoicesInProgressComboBox.getItems().setAll(invoices);
        invoicesInProgressContainer.getChildren().setAll(invoices.stream().map(i -> new Label(i.toString())).toList());
    }

    private void clearCreateInvoiceForm() {
        clientComboBox.getSelectionModel().clearSelection();
        lessonComboBox.getSelectionModel().clearSelection();
        lessonsAmountField.clear();
    }

    private void clearCreateInvoiceErrors() {
        commonInterface.clearErrorLabel(clientComboBoxErrorLabel);
        commonInterface.clearErrorLabel(lessonComboBoxErrorLabel);
        commonInterface.clearErrorLabel(lessonsAmountErrorLabel);
        commonInterface.clearErrorLabel(createNewInvoiceWarningLabel);
    }

    private void clearMarkPaidForm() {
        invoicesInProgressComboBox.getSelectionModel().clearSelection();
        paymentDatePicker.setValue(null);
    }

    private void clearMarkPaidErrors() {
        commonInterface.clearErrorLabel(invoicesInProgressComboBoxErrorLabel);
        commonInterface.clearErrorLabel(paymentDatePickerErrorLabel);
        commonInterface.clearErrorLabel(markAsPaidWarningLabel);
    }
}