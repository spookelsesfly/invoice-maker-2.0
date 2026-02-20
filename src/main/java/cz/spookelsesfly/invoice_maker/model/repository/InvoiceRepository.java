package cz.spookelsesfly.invoice_maker.model.repository;

import cz.spookelsesfly.invoice_maker.model.entity.Client;
import cz.spookelsesfly.invoice_maker.model.entity.Invoice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InvoiceRepository extends BaseRepository<Invoice> {

    public InvoiceRepository() {
        super(Invoice.class);
    }

    public List<Invoice> findAll() {
        return em.createNamedQuery("Invoice.findAll", Invoice.class)
                .getResultList();
    }

    public List<Invoice> findAllByClient(Client client) {
        return em.createNamedQuery("Invoice.findAllByClient", Invoice.class)
                .setParameter("client", client)
                .getResultList();
    }

    public Optional<Invoice> findLatestInvoiceForYear(int year) {
        int start = year * 10000;
        int end = start + 9999;

        return em.createNamedQuery("Invoice.findLatestForYear", Invoice.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst();
    }


    public List<Invoice> findAllUnpaid() {
        return em.createNamedQuery("Invoice.findAllUnpaid", Invoice.class)
                .getResultList();
    }
}
