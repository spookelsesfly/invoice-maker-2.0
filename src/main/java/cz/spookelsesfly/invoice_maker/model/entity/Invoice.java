package cz.spookelsesfly.invoice_maker.model.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@NamedQuery(
        name = "Invoice.findAll",
        query = "SELECT i FROM Invoice i"
)
@NamedQuery(
        name = "Invoice.findAllByClient",
        query = "SELECT i FROM Invoice i WHERE i.client = :client"
)
@NamedQuery(
        name = "Invoice.desc", // used for finding latest invoice
        query = "SELECT i FROM Invoice i ORDER BY i.number DESC"
)
@NamedQuery(
        name = "Invoice.findAllUnpaid",
        query = "SELECT i FROM Invoice i WHERE i.payed = FALSE"
)
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "invoice_id")
    private Integer invoiceId;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "number", nullable = false, unique = true)
    private Integer number;

    @ManyToOne
    @JoinColumn(name = "lesson_type", nullable = false)
    private Lesson lesson;

    @Column(name = "lesson_amount", nullable = false)
    private int lessonAmount;

    @Column(name = "value")
    private Integer value;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "payed")
    private Boolean payed;

    @Column(name = "date_of_payment")
    private LocalDate dateOfPayment;

    @Column(name = "proforma_invoice_path")
    private String proformaInvoicePath;

    @Column(name = "invoice_path")
    private String invoicePath;

    public Invoice() {
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public int getLessonAmount() {
        return lessonAmount;
    }

    public void setLessonAmount(int lessonAmount) {
        this.lessonAmount = lessonAmount;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Boolean getPayed() {
        return payed;
    }

    public void setPayed(Boolean payed) {
        this.payed = payed;
    }

    public LocalDate getDateOfPayment() {
        return dateOfPayment;
    }

    public void setDateOfPayment(LocalDate dateOfPayment) {
        this.dateOfPayment = dateOfPayment;
    }

    public String getProformaInvoicePath() {
        return proformaInvoicePath;
    }

    public void setProformaInvoicePath(String proformaInvoicePath) {
        this.proformaInvoicePath = proformaInvoicePath;
    }

    public String getInvoicePath() {
        return invoicePath;
    }

    public void setInvoicePath(String invoicePath) {
        this.invoicePath = invoicePath;
    }
}
