package cz.spookelsesfly.invoice_maker.model.util;

import cz.spookelsesfly.invoice_maker.model.entity.Invoice;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope("singleton")
public class XlsxBuilder {

    private final Resource template;

    private final QrApiClient qrApiClient;

    public XlsxBuilder(@Value("${invoice.xlsx.template}") Resource template, QrApiClient qrApiClient) {
        this.template = template;
        this.qrApiClient = qrApiClient;
    }

    private Map<String, Cell> extractPlaceholders(Sheet sheet) {
        Map<String, Cell> map = new HashMap<>();
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell.getCellType() == CellType.STRING) {
                    String value = cell.getStringCellValue().trim();
                    if (value.startsWith("{{") && value.endsWith("}}") && value.length() > 4) {
                        String key = value.substring(2, value.length() - 2).trim();
                        if (map.put(key, cell) != null) {
                            throw new IllegalStateException("Duplicate placeholder: {{" + key + "}}");
                        }
                    }
                }
            }
        }
        return map;
    }

    public String createNewXlsxProformaInvoice(Invoice invoice) throws Exception {

        String fileName = "P" + invoice.getNumber() + " - " + invoice.getClient().getLastName() + ".xlsx";

        try (InputStream inputStream = template.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            Map<String, Cell> cells = extractPlaceholders(sheet);

            cells.get("invoice.type").setCellValue("PROFORMA FAKTURA Č.:");
            cells.get("invoice.id").setCellValue("P" + invoice.getNumber());
            cells.get("invoice.date").setCellValue(invoice.getDate().toString());
            cells.get("invoice.datePlus").setCellValue(invoice.getDate().plusDays(14).toString());
            cells.get("lesson.summary.alreadyPayed").setCellValue(0);
            cells.get("lesson.summary.remaining").setCellValue(invoice.getValue());

            cells.get("invoice.variableSymbol").setCellValue(invoice.getNumber());

            cells.get("client.name").setCellValue(invoice.getClient().getFirstName() + " " + invoice.getClient().getLastName());
            cells.get("client.address.firstLine").setCellValue(invoice.getClient().getAddressFirstLine());
            cells.get("client.address.secondLine").setCellValue(invoice.getClient().getAddressSecondLine());
            cells.get("client.address.state").setCellValue(invoice.getClient().getAddressState());

            cells.get("client.phoneNumber").setCellValue(invoice.getClient().getPhone());
            cells.get("client.email").setCellValue(invoice.getClient().getEmail());

            cells.get("lesson.durationMinutes").setCellValue(invoice.getLesson().getDurationMinutes());
            cells.get("lesson.amount").setCellValue(invoice.getLessonAmount());
            cells.get("lesson.price").setCellValue(invoice.getLesson().getPrice());

            cells.get("lesson.summary.total").setCellValue(invoice.getValue());
            cells.get("lesson.summary.totalToPay").setCellValue(invoice.getValue());

            byte[] qrCode = qrApiClient.getQRCode(2720344011L, 3030, invoice.getValue(), invoice.getNumber(), "0308", "Doučování z fyziky.");

            int pictureIndex = workbook.addPicture(qrCode, Workbook.PICTURE_TYPE_PNG);
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
            anchor.setCol1(6);
            anchor.setRow1(19);

            Picture pict = drawing.createPicture(anchor, pictureIndex);
            pict.resize(0.5);

            try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                workbook.write(fileOut);
            }

            return fileName;
        }
    }

    public String createNewXlsxInvoice(Invoice invoice) throws Exception {

        String fileName = "F" + invoice.getNumber() + " - " + invoice.getClient().getLastName() + ".xlsx";

        try (InputStream inputStream = template.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            Map<String, Cell> cells = extractPlaceholders(sheet);

            cells.get("invoice.type").setCellValue("FAKTURA Č.:");
            cells.get("invoice.id").setCellValue(("F") + invoice.getNumber());
            cells.get("invoice.date").setCellValue(invoice.getDateOfPayment().toString());
            cells.get("invoice.datePlus").setCellValue(invoice.getDate().plusDays(14).toString());
            cells.get("lesson.summary.alreadyPayed").setCellValue(invoice.getValue());
            cells.get("lesson.summary.remaining").setCellValue(0);

            cells.get("invoice.variableSymbol").setCellValue(invoice.getNumber());

            cells.get("client.name").setCellValue(invoice.getClient().getFirstName() + " " + invoice.getClient().getLastName());
            cells.get("client.address.firstLine").setCellValue(invoice.getClient().getAddressFirstLine());
            cells.get("client.address.secondLine").setCellValue(invoice.getClient().getAddressSecondLine());
            cells.get("client.address.state").setCellValue(invoice.getClient().getAddressState());

            cells.get("client.phoneNumber").setCellValue(invoice.getClient().getPhone());
            cells.get("client.email").setCellValue(invoice.getClient().getEmail());

            cells.get("lesson.durationMinutes").setCellValue(invoice.getLesson().getDurationMinutes());
            cells.get("lesson.amount").setCellValue(invoice.getLessonAmount());
            cells.get("lesson.price").setCellValue(invoice.getLesson().getPrice());

            cells.get("lesson.summary.total").setCellValue(invoice.getValue());
            cells.get("lesson.summary.totalToPay").setCellValue(invoice.getValue());

            cells.get("lesson.payedMark").setCellValue("Tato faktura byla uhrazena v plné výši");

            byte[] qrCode = qrApiClient.getQRCode(2720344011L, 3030, invoice.getValue(), invoice.getNumber(), "0308", "Doučování z fyziky.");

            int pictureIndex = workbook.addPicture(qrCode, Workbook.PICTURE_TYPE_PNG);
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
            anchor.setCol1(6);
            anchor.setRow1(19);

            Picture pict = drawing.createPicture(anchor, pictureIndex);
            pict.resize(0.5);

            try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                workbook.write(fileOut);
            }

            return fileName;
        }
    }
}
