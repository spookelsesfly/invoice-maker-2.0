package cz.spookelsesfly.invoice_maker.controller;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

@Component
public class CommonInterface {

    public boolean validateRequired(String value, Label errorLabel, String message) {
        if (value == null || value.trim().isEmpty()) {
            showErrorLabel(errorLabel, message);
            return false;
        }
        clearErrorLabel(errorLabel);
        return true;
    }

    public boolean validatePositive(String value, Label errorLabel, String message) {
        try {
            if (Integer.parseInt(value.trim()) < 1) {
                showErrorLabel(errorLabel, message);
                return false;
            }
        } catch (NumberFormatException e) {
            showErrorLabel(errorLabel, message);
            return false;
        }
        clearErrorLabel(errorLabel);
        return true;
    }

    public <T> boolean validateComboBoxSelected(ComboBox<T> comboBox, Label errorLabel, String message) {
        if (comboBox.getValue() == null) {
            showErrorLabel(errorLabel, message);
            return false;
        }
        clearErrorLabel(errorLabel);
        return true;
    }

    public <T> void reloadComboBox(ComboBox<T> comboBox, Supplier<List<T>> dataSupplier) {
        comboBox.getItems().setAll(dataSupplier.get());
    }

    public void setupErrorLabel(Label label) {
        label.setVisible(false);
        label.managedProperty().bind(label.visibleProperty());
    }

    public void showErrorLabel(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
    }

    public void clearErrorLabel(Label label) {
        label.setText("");
        label.setVisible(false);
    }

    public String fillSingleField(String s) {
        return s == null ? "" : s;
    }
}