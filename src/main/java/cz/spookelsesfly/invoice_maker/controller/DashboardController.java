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

    public DashboardController(ClientService clientService,
                               LessonService lessonService,
                               InvoiceService invoiceService) {
        this.clientService = clientService;
        this.lessonService = lessonService;
        this.invoiceService = invoiceService;
    }

    @FXML
    private void initialize() {
        setupErrorLabel(clientComboBoxErrorLabel);
        setupErrorLabel(lessonComboBoxErrorLabel);
        setupErrorLabel(lessonsAmountErrorLabel);
        setupErrorLabel(createNewInvoiceWarningLabel);

        setupErrorLabel(invoicesInProgressComboBoxErrorLabel);
        setupErrorLabel(paymentDatePickerErrorLabel);
        setupErrorLabel(markAsPaidWarningLabel);

        loadClients();
        loadLessons();
        loadInvoicesInProgress();
    }

    @FXML
    private void createNewInvoice() {
        clearCreateInvoiceErrors();

        boolean valid = true;

        valid &= validateComboBoxSelected(clientComboBox, clientComboBoxErrorLabel, "Client is required.");
        valid &= validateComboBoxSelected(lessonComboBox, lessonComboBoxErrorLabel, "Lesson is required.");

        Integer amount = parsePositiveIntOrNull(lessonsAmountField.getText());

        if (amount == null) {
            lessonsAmountErrorLabel.setText("Lessons amount must be greater than 0.");
            lessonsAmountErrorLabel.setVisible(true);
            valid = false;
        } else {
            clearErrorLabel(lessonsAmountErrorLabel);
        }

        if (!valid) {
            createNewInvoiceWarningLabel.setText("Please fix highlighted fields.");
            createNewInvoiceWarningLabel.setVisible(true);
            return;
        }

        try {
            invoiceService.addNewInvoice(clientComboBox.getValue(), lessonComboBox.getValue(), amount);
            loadInvoicesInProgress();
            clearCreateInvoiceForm();

        } catch (Exception e) {
            createNewInvoiceWarningLabel.setText(e.getMessage());
            createNewInvoiceWarningLabel.setVisible(true);
        }
    }

    @FXML
    private void markInvoiceAsPaid() {
        clearMarkPaidErrors();

        boolean valid = true;

        valid &= validateComboBoxSelected(invoicesInProgressComboBox, invoicesInProgressComboBoxErrorLabel, "Invoice is required.");

        if (paymentDatePicker.getValue() == null) {
            paymentDatePickerErrorLabel.setText("Payment date is required.");
            paymentDatePickerErrorLabel.setVisible(true);
            valid = false;
        } else {
            clearErrorLabel(paymentDatePickerErrorLabel);
        }

        if (!valid) {
            markAsPaidWarningLabel.setText("Please fix highlighted fields.");
            markAsPaidWarningLabel.setVisible(true);
            return;
        }

        try {
            invoiceService.markAsPaid(invoicesInProgressComboBox.getValue(), paymentDatePicker.getValue());
            loadInvoicesInProgress();
            clearMarkPaidForm();

        } catch (Exception e) {
            markAsPaidWarningLabel.setText(e.getMessage());
            markAsPaidWarningLabel.setVisible(true);
        }
    }

    private void loadClients() {
        clientComboBox.getItems().setAll(clientService.findAll());
    }

    private void loadLessons() {
        lessonComboBox.getItems().setAll(lessonService.findAll());
    }

    private void loadInvoicesInProgress() {
        List<Invoice> invoices = invoiceService.findAllInProcess();

        invoicesInProgressComboBox.getItems().setAll(invoices);

        invoicesInProgressContainer.getChildren().setAll(invoices.stream().map(i -> new Label(i.toString())).toList());
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

    private void clearCreateInvoiceForm() {
        clientComboBox.getSelectionModel().clearSelection();
        lessonComboBox.getSelectionModel().clearSelection();
        lessonsAmountField.clear();
    }

    private void clearCreateInvoiceErrors() {
        clearErrorLabel(clientComboBoxErrorLabel);
        clearErrorLabel(lessonComboBoxErrorLabel);
        clearErrorLabel(lessonsAmountErrorLabel);
        clearErrorLabel(createNewInvoiceWarningLabel);
    }

    private void clearMarkPaidForm() {
        invoicesInProgressComboBox.getSelectionModel().clearSelection();
        paymentDatePicker.setValue(null);
    }

    private void clearMarkPaidErrors() {
        clearErrorLabel(invoicesInProgressComboBoxErrorLabel);
        clearErrorLabel(paymentDatePickerErrorLabel);
        clearErrorLabel(markAsPaidWarningLabel);
    }

    private void setupErrorLabel(Label label) {
        label.setVisible(false);
        label.managedProperty().bind(label.visibleProperty());
    }

    private void clearErrorLabel(Label label) {
        label.setText("");
        label.setVisible(false);
    }

    private Integer parsePositiveIntOrNull(String s) {
        try {
            int n = Integer.parseInt(s.trim());
            return n > 0 ? n : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}