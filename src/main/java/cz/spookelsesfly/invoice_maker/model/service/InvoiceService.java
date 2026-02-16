package cz.spookelsesfly.invoice_maker.model.service;

import cz.spookelsesfly.invoice_maker.model.entity.Client;
import cz.spookelsesfly.invoice_maker.model.entity.Invoice;
import cz.spookelsesfly.invoice_maker.model.entity.Lesson;
import cz.spookelsesfly.invoice_maker.model.repository.InvoiceRepository;
import cz.spookelsesfly.invoice_maker.model.util.XlsxBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    private final XlsxBuilder xlsxBuilder;

    public InvoiceService(InvoiceRepository invoiceRepository, XlsxBuilder xlsxBuilder) {
        this.invoiceRepository = invoiceRepository;
        this.xlsxBuilder = xlsxBuilder;
    }

    public int generateInvoiceNumber() {
        Optional<Invoice> latest = invoiceRepository.findLatestInvoice();
        return latest.map(invoice -> invoice.getNumber() + 1).orElseGet(() -> Integer.parseInt(LocalDate.now().getYear() + "0001")); // A little bit of functional programming
    }

    public void addNewInvoice (Client client, Lesson lessonType, int lessonAmount) throws Exception {
        Objects.requireNonNull(client);
        Objects.requireNonNull(lessonType);

        Invoice invoice = new Invoice();

        invoice.setNumber(generateInvoiceNumber());
        invoice.setClient(client);
        invoice.setDate(LocalDate.now());
        invoice.setLesson(lessonType);
        invoice.setLessonAmount(lessonAmount);
        invoice.setValue(lessonType.getPrice() * lessonAmount);

        String xlsxProformaInvoicePath = xlsxBuilder.createNewXlsxProformaInvoice(invoice);
        invoice.setProformaInvoicePath(xlsxProformaInvoicePath);

        invoiceRepository.persist(invoice);
    }

    public void payForInvoice(Invoice invoice, LocalDate dateOfPayment) throws Exception {
        invoice.setPayed(true);
        invoice.setDateOfPayment(dateOfPayment);

        String xlsxInvoicePath = xlsxBuilder.createNewXlsxInvoice(invoice);
        invoice.setInvoicePath(xlsxInvoicePath);

        invoiceRepository.merge(invoice);
    }
}
