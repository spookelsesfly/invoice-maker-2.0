package cz.spookelsesfly.invoice_maker.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LayoutController {

    private final ApplicationContext applicationContext;

    public LayoutController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @FXML
    private StackPane contentPane;

    @FXML
    private void loadDashboard(ActionEvent event) throws IOException {
        loadContent("/dashboard.fxml");
    }

    @FXML
    private void loadClients(ActionEvent event) throws IOException {
        loadContent("/clients.fxml");
    }

    @FXML
    private void loadInvoices(ActionEvent event) throws IOException {
        loadContent("/invoices.fxml");
    }

    @FXML
    private void loadSettings(ActionEvent event) throws IOException {
        loadContent("/settings.fxml");
    }

    private void loadContent(String fxml) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));

        loader.setControllerFactory(applicationContext::getBean);

        Node view = loader.load();

        contentPane.getChildren().setAll(view);
    }
}
