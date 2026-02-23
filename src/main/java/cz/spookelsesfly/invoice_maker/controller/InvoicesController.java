package cz.spookelsesfly.invoice_maker.controller;

import cz.spookelsesfly.invoice_maker.model.entity.Client;
import cz.spookelsesfly.invoice_maker.model.entity.Invoice;
import cz.spookelsesfly.invoice_maker.model.service.ClientService;
import cz.spookelsesfly.invoice_maker.model.service.InvoiceService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Component
public class InvoicesController {

    @FXML
    private ComboBox<Client> clientsComboBox;

    @FXML
    private VBox infoBox;

    @FXML
    private VBox invoicesBox;

    @FXML
    private Label warningLabel;

    private final ClientService clientService;
    private final InvoiceService invoiceService;
    private final CommonInterface commonInterface;

    private final NumberFormat czNumberFormat;

    public InvoicesController(ClientService clientService,
                              InvoiceService invoiceService,
                              CommonInterface commonInterface) {
        this.clientService = clientService;
        this.invoiceService = invoiceService;
        this.commonInterface = commonInterface;
        this.czNumberFormat = NumberFormat.getInstance(new Locale("cs", "CZ"));
    }

    @FXML
    private void initialize() {
        commonInterface.setupErrorLabel(warningLabel);
        loadClients();
        showAllInvoices();
    }

    private void loadClients() {
        clientsComboBox.getItems().setAll(clientService.findAll());
    }

    @FXML
    private void showAllInvoices() {
        commonInterface.clearErrorLabel(warningLabel);

        clientsComboBox.getSelectionModel().clearSelection();
        showAllInfo();

        List<Invoice> invoices = invoiceService.findAll();
        addInvoices(invoices);
    }

    @FXML
    private void showClientInvoices() {
        commonInterface.clearErrorLabel(warningLabel);

        Client client = clientsComboBox.getValue();
        if (client == null) {
            return;
        }

        addClientInfo(client);

        List<Invoice> invoices = invoiceService.findAllByClient(client);
        addInvoices(invoices);
    }

    private void showAllInfo() {
        infoBox.getChildren().clear();
        infoBox.getChildren().add(new Label("Showing all invoices"));
    }

    private void addClientInfo(Client client) {
        infoBox.getChildren().clear();
        infoBox.getChildren().addAll(
                new Label(client.toString()),
                new Label(client.getEmail()),
                new Label(client.getPhone()),
                new Label(client.getAddressFirstLine()),
                new Label(client.getAddressSecondLine()),
                new Label(client.getAddressState())
        );
    }

    private void addInvoices(List<Invoice> invoices) {
        invoicesBox.getChildren().clear();

        if (invoices == null || invoices.isEmpty()) {
            invoicesBox.getChildren().add(new Label("No invoices found."));
            return;
        }

        for (Invoice invoice : invoices) {
            invoicesBox.getChildren().add(invoiceAsLabel(invoice));
        }
    }

    private Node invoiceAsLabel(Invoice invoice) {
        Label number = new Label(String.valueOf(invoice.getNumber()));
        number.getStyleClass().add(invoice.isPayed() ? "green" : "red");

        String value = czNumberFormat.format(invoice.getValue());
        Label info = new Label(" - " +
                invoice.getClient().getLastName()
                + " - "
                + value
                + " Kč ");

        HBox labelBox = new HBox(number, info);
        labelBox.getStyleClass().add("center");

        Button proformaInvoiceButton = createInvoiceButton("p-invoice", invoice.getProformaInvoicePath());

        HBox row = new HBox(labelBox, proformaInvoiceButton);
        row.getStyleClass().add("marginD");

        if (invoice.isPayed()) {
            Button invoiceButton = createInvoiceButton("invoice", invoice.getInvoicePath());
            row.getChildren().add(invoiceButton);
        }

        return row;
    }

    private Button createInvoiceButton(String text, String path) {
        Button button = new Button(text);
        button.getStyleClass().add("button-special");
        button.setOnAction(e -> openInvoice(path));
        return button;
    }

    private void openInvoice(String relativePath) {
        commonInterface.clearErrorLabel(warningLabel);

        if (relativePath == null || relativePath.isBlank()) {
            commonInterface.showErrorLabel(warningLabel, "Invoice file path is empty.");
            return;
        }

        File file = new File(System.getProperty("user.dir"), relativePath);

        if (!file.exists()) {
            commonInterface.showErrorLabel(warningLabel, "File does not exist: " + file.getAbsolutePath());
            return;
        }

        if (!Desktop.isDesktopSupported()) {
            commonInterface.showErrorLabel(warningLabel, "Opening files is not supported on this system.");
            return;
        }

        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            commonInterface.showErrorLabel(warningLabel, "Failed to open file: " + file.getAbsolutePath());
        }
    }
}