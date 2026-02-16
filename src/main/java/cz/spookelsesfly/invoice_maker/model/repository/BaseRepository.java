package cz.spookelsesfly.invoice_maker.model.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

// Parent repository for other specific repositories
public abstract class BaseRepository<T> {

    @PersistenceContext
    protected EntityManager em;
    protected final Class<T> entityClass;

    protected BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public Optional<T> find(Integer id) {
        return Optional.ofNullable(em.find(entityClass, id));
    }

    public void persist(T entity) {
        em.persist(entity);
    }

    public T merge(T entity) {
        return em.merge(entity); // returns managed instance
    }

    public void remove(T entity) {
        // if managed, then remove, if detached, merge and then delete
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
