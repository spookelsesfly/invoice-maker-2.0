package cz.spookelsesfly.invoice_maker.model.repository;

import cz.spookelsesfly.invoice_maker.model.entity.Client;
import cz.spookelsesfly.invoice_maker.model.entity.Invoice;
import cz.spookelsesfly.invoice_maker.model.entity.Lesson;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClientRepository extends BaseRepository<Client> {

    public ClientRepository() {
        super(Client.class);
    }

    public List<Client> findAll(){
        return em.createNamedQuery("Client.findAll", Client.class)
                .getResultList();
    }
}
