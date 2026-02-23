package cz.spookelsesfly.invoice_maker.model.service;

import cz.spookelsesfly.invoice_maker.model.entity.Client;
import cz.spookelsesfly.invoice_maker.model.entity.Invoice;
import cz.spookelsesfly.invoice_maker.model.entity.Lesson;
import cz.spookelsesfly.invoice_maker.model.exception.InvoiceValidationException;
import cz.spookelsesfly.invoice_maker.model.repository.ClientRepository;
import cz.spookelsesfly.invoice_maker.model.repository.InvoiceRepository;
import cz.spookelsesfly.invoice_maker.model.repository.LessonRepository;
import cz.spookelsesfly.invoice_maker.model.util.XlsxBuilder;
import cz.spookelsesfly.invoice_maker.model.validation.InvoiceValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final LessonRepository lessonRepository;
    private final InvoiceValidator invoiceValidator;
    private final XlsxBuilder xlsxBuilder;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          ClientRepository clientRepository,
                          LessonRepository lessonRepository,
                          InvoiceValidator invoiceValidator,
                          XlsxBuilder xlsxBuilder) {
        this.invoiceRepository = invoiceRepository;
        this.clientRepository = clientRepository;
        this.lessonRepository = lessonRepository;
        this.invoiceValidator = invoiceValidator;
        this.xlsxBuilder = xlsxBuilder;
    }

    public int generateInvoiceNumber() {
        int year = LocalDate.now().getYear();

        return invoiceRepository.findLatestInvoiceForYear(year)
                .map(inv -> inv.getNumber() + 1)
                .orElse(year * 10000 + 1);
    }

    public void addNewInvoice(Client client, Lesson lessonType, int lessonAmount) throws Exception {
        Invoice invoice = new Invoice();
        invoice.setNumber(generateInvoiceNumber());
        invoice.setClient(client);
        invoice.setDate(LocalDate.now());
        invoice.setLesson(lessonType);
        invoice.setLessonAmount(lessonAmount);
        invoice.setValue(lessonType.getPrice() * lessonAmount);

        invoiceValidator.validateInvoice(invoice);

        doesClientExist(client);
        doesLessonExist(lessonType);

        String xlsxProformaInvoicePath = xlsxBuilder.createNewXlsxProformaInvoice(invoice);
        invoice.setProformaInvoicePath(xlsxProformaInvoicePath);

        invoiceRepository.persist(invoice);
    }

    public void updateInvoice(Invoice invoice) throws Exception {
        invoiceValidator.validateInvoice(invoice);

        String xlsxProformaInvoicePath = xlsxBuilder.createNewXlsxProformaInvoice(invoice);
        invoice.setProformaInvoicePath(xlsxProformaInvoicePath);

        if (invoice.isPayed()) {
            String xlsxInvoicePath = xlsxBuilder.createNewXlsxInvoice(invoice);
            invoice.setInvoicePath(xlsxInvoicePath);
        }

        invoiceRepository.merge(invoice);
    }

    public void markAsPaid(Invoice invoice, LocalDate dateOfPayment) throws Exception {
        invoice.setPayed(true);
        invoice.setDateOfPayment(dateOfPayment);

        invoiceValidator.validateInvoice(invoice);

        String xlsxInvoicePath = xlsxBuilder.createNewXlsxInvoice(invoice);
        invoice.setInvoicePath(xlsxInvoicePath);

        invoiceRepository.merge(invoice);
    }

    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    public List<Invoice> findAllInProcess() {
        return invoiceRepository.findAllUnpaid();
    }

    public List<Invoice> findAllByClient(Client client) {
        return invoiceRepository.findAllByClient(client);
    }

    private void doesClientExist(Client client) {
        if (clientRepository.findByFirstNameAndLastName(client.getFirstName(), client.getLastName()).isEmpty()) {
            throw new InvoiceValidationException("Client does not exist in database.");
        }
    }

    private void doesLessonExist(Lesson lesson) {
        if (lessonRepository.findByType(lesson.getType()).isEmpty()) {
            throw new InvoiceValidationException("Lesson does not exist in database.");
        }
    }
}