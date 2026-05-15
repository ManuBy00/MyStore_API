package com.mby.myStore.Services;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import com.lowagie.text.pdf.draw.LineSeparator;
import com.mby.myStore.Model.Invoice;
import com.mby.myStore.Repositories.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PdfService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    // --- Configuración de Estilo (Constantes) ---
    private final Color BRAND_ORANGE = new Color(248, 153, 0);
    private final Color DARK_BLUE = new Color(17, 26, 45);

    private final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, DARK_BLUE);
    private final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
    private final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, DARK_BLUE);
    private final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
    private final Font SMALL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.DARK_GRAY); // Para dirección y CIF

    /**
     * GENERA LA FACTURA INDIVIDUAL
     */
    public byte[] generateInvoicePdf(Invoice invoice) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, out);
        document.open();

        // 1. CABECERA
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);

        // Lado Izquierdo: Barbería
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.addElement(new Paragraph("BARBER STORE", TITLE_FONT));
        Paragraph addr = new Paragraph("Calle del Estilo, 123\n28001 Madrid\nCIF: B12345678", SMALL_FONT);
        addr.setSpacingBefore(5f);
        leftCell.addElement(addr);
        headerTable.addCell(leftCell);

        // Lado Derecho: Info Factura
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Paragraph fTitle = new Paragraph("FACTURA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BRAND_ORANGE));
        fTitle.setAlignment(Element.ALIGN_RIGHT);
        rightCell.addElement(fTitle);
        Paragraph fData = new Paragraph("Nº: " + invoice.getInvoiceNumber() + "\nFecha: " + invoice.getDate(), NORMAL_FONT);
        fData.setAlignment(Element.ALIGN_RIGHT);
        rightCell.addElement(fData);
        headerTable.addCell(rightCell);

        document.add(headerTable);
        document.add(new Paragraph(" "));
        document.add(new LineSeparator(1, 100, BRAND_ORANGE, Element.ALIGN_CENTER, -2));
        document.add(new Paragraph(" "));

        // 2. CLIENTE Y EMPLEADO
        PdfPTable clientTable = new PdfPTable(2);
        clientTable.setWidthPercentage(100);
        clientTable.addCell(createSimpleCell("FACTURADO A:", BOLD_FONT, Element.ALIGN_LEFT));
        clientTable.addCell(createSimpleCell("ATENDIDO POR:", BOLD_FONT, Element.ALIGN_RIGHT));
        clientTable.addCell(createSimpleCell(invoice.getClientName(), NORMAL_FONT, Element.ALIGN_LEFT));
        clientTable.addCell(createSimpleCell(invoice.getEmployeeName(), NORMAL_FONT, Element.ALIGN_RIGHT));
        document.add(clientTable);
        document.add(new Paragraph("\n\n"));

        // 3. TABLA DE PRODUCTOS
        PdfPTable itemsTable = new PdfPTable(4);
        itemsTable.setWidthPercentage(100);
        itemsTable.setWidths(new float[]{3, 1, 1, 1});

        // Encabezados
        String[] headers = {"Descripción", "Base Imp.", "IVA (21%)", "Subtotal"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Paragraph(h, HEADER_FONT));
            cell.setBackgroundColor(BRAND_ORANGE);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            itemsTable.addCell(cell);
        }

        // Fila Única (Servicio)
        double total = invoice.getPrice().doubleValue();
        double base = total / 1.21;
        double iva = total - base;

        itemsTable.addCell(createBorderBottomCell(invoice.getServiceName(), NORMAL_FONT, Element.ALIGN_LEFT));
        itemsTable.addCell(createBorderBottomCell(String.format("%.2f€", base), NORMAL_FONT, Element.ALIGN_RIGHT));
        itemsTable.addCell(createBorderBottomCell(String.format("%.2f€", iva), NORMAL_FONT, Element.ALIGN_RIGHT));
        itemsTable.addCell(createBorderBottomCell(String.format("%.2f€", total), NORMAL_FONT, Element.ALIGN_RIGHT));
        document.add(itemsTable);

        // 4. TOTAL
        document.add(new Paragraph(" "));
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(35);
        totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

        totalTable.addCell(createSimpleCell("Total:", TITLE_FONT, Element.ALIGN_LEFT));
        PdfPCell totalVal = createSimpleCell(String.format("%.2f €", total), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BRAND_ORANGE), Element.ALIGN_RIGHT);
        totalTable.addCell(totalVal);
        document.add(totalTable);

        // 5. FOOTER
        Paragraph footer = new Paragraph("\n\n\n¡Gracias por confiar en nosotros! Esperamos verte pronto.", NORMAL_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return out.toByteArray();
    }

    /**
     * GENERA EL REPORTE MENSUAL
     */
    public byte[] generateMonthlyReport(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        List<Invoice> invoices = invoiceRepository.findByDateBetween(start, start.withDayOfMonth(start.lengthOfMonth()));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
        PdfWriter.getInstance(document, out);
        document.open();

        // Título Reporte
        Paragraph t = new Paragraph("INFORME MENSUAL DE FACTURACIÓN", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, DARK_BLUE));
        t.setAlignment(Element.ALIGN_CENTER);
        document.add(t);
        Paragraph st = new Paragraph("Periodo: " + month + "/" + year, NORMAL_FONT);
        st.setAlignment(Element.ALIGN_CENTER);
        document.add(st);
        document.add(new Paragraph(" "));

        // Tabla
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.2f, 2.5f, 2.5f, 1.2f, 1.2f, 1.2f});

        // --- ENCABEZADOS (Sin bordes negros feos) ---
        String[] headers = {"Fecha", "Cliente", "Servicio", "Base Imp.", "IVA (21%)", "Total"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Paragraph(h, HEADER_FONT));
            cell.setBackgroundColor(BRAND_ORANGE);
            cell.setBorderColor(BRAND_ORANGE); // El borde es del mismo color que el fondo
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        double tBase = 0, tIva = 0, tTotal = 0;
        for (Invoice inv : invoices) {
            double val = inv.getPrice().doubleValue();
            double base = val / 1.21;
            double iva = val - base;
            tBase += base; tIva += iva; tTotal += val;

            // --- FILAS (Usando un nuevo helper para consistencia) ---
            table.addCell(createBorderBottomCell(inv.getDate().toString(), NORMAL_FONT, Element.ALIGN_LEFT));
            table.addCell(createBorderBottomCell(inv.getClientName(), NORMAL_FONT, Element.ALIGN_LEFT));
            table.addCell(createBorderBottomCell(inv.getServiceName(), NORMAL_FONT, Element.ALIGN_LEFT));
            table.addCell(createBorderBottomCell(String.format("%.2f€", base), NORMAL_FONT, Element.ALIGN_RIGHT));
            table.addCell(createBorderBottomCell(String.format("%.2f€", iva), NORMAL_FONT, Element.ALIGN_RIGHT));
            table.addCell(createBorderBottomCell(String.format("%.2f€", val), NORMAL_FONT, Element.ALIGN_RIGHT));
        }
        document.add(table);

        // Resumen
        document.add(new Paragraph(" "));
        PdfPTable summary = new PdfPTable(2);
        summary.setWidthPercentage(35);
        summary.setHorizontalAlignment(Element.ALIGN_RIGHT);

        addSummaryRow(summary, "Total Base:", String.format("%.2f €", tBase));
        addSummaryRow(summary, "Total IVA:", String.format("%.2f €", tIva));
        addSummaryRow(summary, "TOTAL:", String.format("%.2f €", tTotal));
        document.add(summary);

        document.close();
        return out.toByteArray();
    }

    // --- HELPERS (Métodos de ayuda para no repetir código) ---

    private PdfPCell createSimpleCell(String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(align);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell createBorderBottomCell(String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(new Color(230, 230, 230));
        cell.setPadding(10);
        cell.setHorizontalAlignment(align);
        return cell;
    }

    private void addSummaryRow(PdfPTable table, String label, String value) {
        table.addCell(createSimpleCell(label, NORMAL_FONT, Element.ALIGN_LEFT));
        table.addCell(createSimpleCell(value, BOLD_FONT, Element.ALIGN_RIGHT));
    }
}