package cz.spookelsesfly.invoice_maker.model.validation;

import cz.spookelsesfly.invoice_maker.model.entity.Lesson;
import cz.spookelsesfly.invoice_maker.model.exception.LessonValidationException;
import org.springframework.stereotype.Component;

@Component
public class LessonValidator {

    public void validateLesson(Lesson lesson) {
        if (lesson == null) {
            throw new LessonValidationException("Lesson is null.");
        }

        validateRequired(lesson.getType(), "Lesson type is required.");
        validateNonNullPositive(lesson.getDurationMinutes(), "Duration must be greater than 1.");
        validateNonNullPositive(lesson.getPrice(), "Price must be greater than 1.");
    }

    public void validateRequired(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new LessonValidationException(message);
        }
    }

    public void validateNonNullPositive(int value, String message) {
        if (value < 1) {
            throw new LessonValidationException(message);
        }
    }
}