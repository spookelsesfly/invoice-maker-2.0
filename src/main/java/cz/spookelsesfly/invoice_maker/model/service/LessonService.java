package cz.spookelsesfly.invoice_maker.model.service;

import cz.spookelsesfly.invoice_maker.model.entity.Lesson;
import cz.spookelsesfly.invoice_maker.model.exception.LessonValidationException;
import cz.spookelsesfly.invoice_maker.model.repository.LessonRepository;
import cz.spookelsesfly.invoice_maker.model.validation.LessonValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonValidator lessonValidator;

    public LessonService(LessonRepository lessonRepository, LessonValidator lessonValidator) {
        this.lessonRepository = lessonRepository;
        this.lessonValidator = lessonValidator;
    }

    public void addNewLesson(Lesson lesson) {
        lessonValidator.validateLesson(lesson);
        ifAlreadyExist(lesson);
        lessonRepository.persist(lesson);
    }

    public void updateLesson(Lesson lesson) {
        lessonValidator.validateLesson(lesson);
        lessonRepository.merge(lesson);
    }

    public List<Lesson> findAll() {
        return lessonRepository.findAll();
    }

    private void ifAlreadyExist(Lesson newLesson) {
        if (lessonRepository.findByType(newLesson.getType()).isPresent()) {
            throw new LessonValidationException("Lesson with this type already exist in database.");
        }
    }
}