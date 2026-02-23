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
    private final CommonInterface commonInterface;

    public LessonManagementController(LessonService lessonService, CommonInterface commonInterface) {
        this.lessonService = lessonService;
        this.commonInterface = commonInterface;
    }

    @FXML
    private void initialize() {
        commonInterface.setupErrorLabel(warningLabel);

        commonInterface.setupErrorLabel(lessonNameErrorLabel);
        commonInterface.setupErrorLabel(lessonDurationErrorLabel);
        commonInterface.setupErrorLabel(priceErrorLabel);

        addNewLesson();
        commonInterface.reloadComboBox(lessonsToUpdateComboBox, lessonService::findAll);
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

        valid &= commonInterface.validateRequired(
                lessonNameField.getText(),
                lessonNameErrorLabel,
                "Name is required.");

        boolean durationRequired = commonInterface.validateRequired(
                lessonDurationField.getText(),
                lessonDurationErrorLabel,
                "Duration is required.");

        valid &= durationRequired;
        if (durationRequired) {
            valid &= commonInterface.validatePositive(
                    lessonDurationField.getText(),
                    lessonDurationErrorLabel,
                    "Duration must be greater than 1.");
        }

        boolean priceRequired = commonInterface.validateRequired(priceField.getText(),
                priceErrorLabel,
                "Price is required.");

        valid &= priceRequired;
        if (priceRequired) {
            valid &= commonInterface.validatePositive(priceField.getText(),
                    priceErrorLabel,
                    "Price must be greater than 1.");
        }

        if (!valid) {
            commonInterface.showErrorLabel(warningLabel, "Please fix highlighted fields.");
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
            commonInterface.showErrorLabel(warningLabel, e.getMessage());
            return;
        }

        clearForm();
        commonInterface.reloadComboBox(lessonsToUpdateComboBox, lessonService::findAll);
        lessonsToUpdateComboBox.getSelectionModel().clearSelection();
        updateButton.setText("Add new lesson");
    }

    private boolean isUpdate() {
        return !lessonsToUpdateComboBox.getSelectionModel().isEmpty();
    }

    private void fillForm(Lesson lesson) {
        lessonNameField.setText(commonInterface.fillSingleField(lesson.getType()));
        lessonDurationField.setText(String.valueOf(lesson.getDurationMinutes()));
        priceField.setText(String.valueOf(lesson.getPrice()));
    }

    private void clearForm() {
        lessonNameField.clear();
        lessonDurationField.clear();
        priceField.clear();
    }

    private void clearErrors() {
        commonInterface.clearErrorLabel(lessonNameErrorLabel);
        commonInterface.clearErrorLabel(lessonDurationErrorLabel);
        commonInterface.clearErrorLabel(priceErrorLabel);
        commonInterface.clearErrorLabel(warningLabel);
    }
}