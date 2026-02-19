package cz.spookelsesfly.invoice_maker.model.repository;

import cz.spookelsesfly.invoice_maker.model.entity.Client;
import cz.spookelsesfly.invoice_maker.model.entity.Invoice;
import cz.spookelsesfly.invoice_maker.model.entity.Lesson;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ClientRepository extends BaseRepository<Client> {

    public ClientRepository() {
        super(Client.class);
    }

    public List<Client> findAll(){
        return em.createNamedQuery("Client.findAll", Client.class)
                .getResultList();
    }

    public Optional<Client> findByFirstNameAndLastName(String firstName, String lastName) {
        try {
            return Optional.of(em.createNamedQuery("Client.findByFirstNameAndLastName", Client.class)
                    .setParameter("firstName", firstName)
                    .setParameter("lastName", lastName)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
