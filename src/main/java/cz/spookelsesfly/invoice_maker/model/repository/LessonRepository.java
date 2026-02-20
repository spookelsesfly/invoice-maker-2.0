package cz.spookelsesfly.invoice_maker.model.repository;

import cz.spookelsesfly.invoice_maker.model.entity.Client;
import cz.spookelsesfly.invoice_maker.model.entity.Lesson;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LessonRepository extends BaseRepository<Lesson> {

    public LessonRepository() {
        super(Lesson.class);
    }

    public List<Lesson> findAll(){
        return em.createNamedQuery("Lesson.findAll", Lesson.class)
                .getResultList();
    }

    public Optional<Lesson> findByType(String type) {
        try {
            return Optional.of(em.createNamedQuery("Lesson.findByType", Lesson.class)
                    .setParameter("type", type)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
