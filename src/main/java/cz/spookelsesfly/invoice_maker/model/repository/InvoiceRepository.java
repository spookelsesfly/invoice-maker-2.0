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

    public List<Invoice> findAllInvoices() {
        return em.createNamedQuery("Invoice.findAll", Invoice.class)
                .getResultList();
    }

    public List<Invoice> findAllInvoicesByClient(Client client) {
        return em.createNamedQuery("Invoice.findAllByClient", Invoice.class)
                .setParameter("client", client)
                .getResultList();
    }

    public Optional<Invoice> findLatestInvoice() {
        return em.createNamedQuery("Invoice.desc", Invoice.class)
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst();
    }

    public List<Invoice> findAllUnpaidInvoices() {
        return em.createNamedQuery("Invoice.findAllUnpaid", Invoice.class)
                .getResultList();
    }
}
