package cz.spookelsesfly.invoice_maker.model.repository;

import cz.spookelsesfly.invoice_maker.model.entity.Lesson;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LessonRepository extends BaseRepository<Lesson> {

    public LessonRepository() {
        super(Lesson.class);
    }

    public List<Lesson> findAll(){
        return em.createNamedQuery("Lesson.findAll", Lesson.class)
                .getResultList();
    }
}
