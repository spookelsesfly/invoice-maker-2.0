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
        validatePositive(lesson.getDurationMinutes(), "Duration must be greater than 0.");
        validatePositive(lesson.getPrice(), "Price must be greater than 0.");
    }

    public void validateRequired(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new LessonValidationException(message);
        }
    }

    public void validatePositive(int value, String message) {
        if (value < 1) {
            throw new LessonValidationException(message);
        }
    }
}