package com.mby.myStore.Services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mby.myStore.Model.Invoice;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    public byte[] generateInvoicePdf(Invoice invoice) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        // Fuentes
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        // Título y Encabezado
        document.add(new Paragraph("FACTURA: " + invoice.getInvoiceNumber(), titleFont));
        document.add(new Paragraph("Fecha: " + invoice.getDate(), normalFont));
        document.add(new Paragraph("--------------------------------------------------"));

        // Información del Cliente y Empleado
        document.add(new Paragraph("Cliente: " + invoice.getAppointment().getUser().getName(), normalFont));
        document.add(new Paragraph("Atendido por: " + invoice.getAppointment().getEmployee().getName(), normalFont));
        document.add(new Paragraph(" ")); // Espacio en blanco

        // Tabla de productos/servicios
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.addCell("Servicio");
        table.addCell("Precio");

        table.addCell(invoice.getAppointment().getService().getName());
        table.addCell(invoice.getTotal().toString() + "€");

        document.add(table);

        // Total final
        Paragraph total = new Paragraph("\nTOTAL: " + invoice.getTotal() + "€", titleFont);
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);

        document.close();
        return out.toByteArray();
    }
}