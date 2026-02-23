package cz.spookelsesfly.invoice_maker.model.validation;

import cz.spookelsesfly.invoice_maker.model.entity.Lesson;
import cz.spookelsesfly.invoice_maker.model.exception.LessonValidationException;
import org.springframework.stereotype.Component;

@Component
public class LessonValidator {

    private final CommonValidator commonValidator;

    public LessonValidator(CommonValidator commonValidator) {
        this.commonValidator = commonValidator;
    }

    public void validateLesson(Lesson lesson) {
        commonValidator.requireNotNull(lesson, () -> new LessonValidationException("Lesson is null."));

        commonValidator.requireRequired(lesson.getType(),
                () -> new LessonValidationException("Lesson type is required."));
        commonValidator.requirePositive(lesson.getDurationMinutes(),
                () -> new LessonValidationException("Duration must be greater than 0."));
        commonValidator.requirePositive(lesson.getPrice(),
                () -> new LessonValidationException("Price must be greater than 0."));
    }
}