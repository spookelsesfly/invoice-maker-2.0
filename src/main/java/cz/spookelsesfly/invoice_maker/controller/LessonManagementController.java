package cz.spookelsesfly.invoice_maker.controller;

import cz.spookelsesfly.invoice_maker.model.entity.Lesson;
import cz.spookelsesfly.invoice_maker.model.exception.LessonValidationException;
import cz.spookelsesfly.invoice_maker.model.service.LessonService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

@Component
public class LessonManagementController {

    @FXML
    private TextField lessonNameField;

    @FXML
    private Label lessonNameErrorLabel;

    @FXML
    private TextField lessonDurationField;

    @FXML
    private Label lessonDurationErrorLabel;

    @FXML
    private TextField priceField;

    @FXML
    private Label priceErrorLabel;

    @FXML
    private Label warningLabel;

    @FXML
    private ComboBox<Lesson> lessonsToUpdateComboBox;

    @FXML
    private Button updateButton;

    private final LessonService lessonService;

    public LessonManagementController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @FXML
    private void initialize() {
        setupErrorLabel(warningLabel);

        setupErrorLabel(lessonNameErrorLabel);
        setupErrorLabel(lessonDurationErrorLabel);
        setupErrorLabel(priceErrorLabel);

        addNewLesson();
        reloadLessonsComboBox();
    }

    @FXML
    private void addNewLesson() {
        lessonsToUpdateComboBox.getSelectionModel().clearSelection();
        clearForm();
        clearErrors();
        warningLabel.setVisible(false);
        updateButton.setText("Add new lesson");
    }

    @FXML
    public void fillUpdateForm() {
        clearErrors();
        warningLabel.setVisible(false);

        Lesson lesson = lessonsToUpdateComboBox.getValue();
        if (lesson == null) {
            clearForm();
            updateButton.setText("Add new lesson");
            return;
        }

        fillForm(lesson);
        updateButton.setText("Update " + lesson);
    }

    @FXML
    private void update() {
        clearErrors();

        boolean valid = true;

        valid &= validateRequired(lessonNameField.getText(), lessonNameErrorLabel, "Name is required.");

        boolean durationRequired = validateRequired(lessonDurationField.getText(), lessonDurationErrorLabel, "Duration is required.");
        valid &= durationRequired;
        if (durationRequired) {
            valid &= validateNonNullPositive(lessonDurationField.getText(), lessonDurationErrorLabel, "Duration must be greater than 1.");
        }

        boolean priceRequired = validateRequired(priceField.getText(), priceErrorLabel, "Price is required.");
        valid &= priceRequired;
        if (priceRequired) {
            valid &= validateNonNullPositive(priceField.getText(), priceErrorLabel, "Price must be greater than 1.");
        }

        if (!valid) {
            warningLabel.setText("Please fix highlighted fields.");
            warningLabel.setVisible(true);
            return;
        }

        Lesson lesson = isUpdate() ? lessonsToUpdateComboBox.getValue() : new Lesson();

        lesson.setType(lessonNameField.getText().trim());
        lesson.setDurationMinutes(Integer.parseInt(lessonDurationField.getText().trim()));
        lesson.setPrice(Integer.parseInt(priceField.getText().trim()));

        try {
            if (isUpdate()) {
                lessonService.updateLesson(lesson);
            } else {
                lessonService.addNewLesson(lesson);
            }
        } catch (LessonValidationException e) {
            warningLabel.setText(e.getMessage());
            warningLabel.setVisible(true);
            return;
        }

        clearForm();
        reloadLessonsComboBox();
        lessonsToUpdateComboBox.getSelectionModel().clearSelection();
        updateButton.setText("Add new lesson");
    }

    private boolean isUpdate() {
        return !lessonsToUpdateComboBox.getSelectionModel().isEmpty();
    }

    private void fillForm(Lesson lesson) {
        lessonNameField.setText(fillSingleField(lesson.getType()));
        lessonDurationField.setText(String.valueOf(lesson.getDurationMinutes()));
        priceField.setText(String.valueOf(lesson.getPrice()));
    }

    private void clearForm() {
        lessonNameField.clear();
        lessonDurationField.clear();
        priceField.clear();
    }

    private void clearErrors() {
        clearErrorLabel(lessonNameErrorLabel);
        clearErrorLabel(lessonDurationErrorLabel);
        clearErrorLabel(priceErrorLabel);
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

    private boolean validateNonNullPositive(String value, Label errorLabel, String message) {
        try {
            if (Integer.parseInt(value.trim()) < 1) {
                errorLabel.setText(message);
                errorLabel.setVisible(true);
                return false;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText(message);
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

    private void reloadLessonsComboBox() {
        lessonsToUpdateComboBox.getItems().setAll(lessonService.findAll());
    }

    private String fillSingleField(String s) {
        return s == null ? "" : s;
    }
}
