package com.mby.myStore.Controllers;

import com.mby.myStore.DTO.BillCardsData;
import com.mby.myStore.DTO.InvoiceRequest;
import com.mby.myStore.DTO.InvoiceResponse;
import com.mby.myStore.Model.Invoice;
import com.mby.myStore.Services.InvoicesService;
import com.mby.myStore.Services.PdfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/billing")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoicesService invoiceService;
    private final PdfService pdfService;

    @PostMapping
    public ResponseEntity<InvoiceResponse> create(@Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.createInvoice(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build(); // Devuelve un 204 No Content
    }

    @GetMapping("/by-date/{date}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<InvoiceResponse> invoices = invoiceService.getInvoicesByDate(date);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getById(id));
    }

    @GetMapping("/{id}/export-pdf")
    public ResponseEntity<byte[]> exportToPdf(@PathVariable Long id) {
        // 1. Buscamos la factura
        Invoice invoice = invoiceService.getInvoiceEntityById(id);

        // 2. Generamos los bytes del PDF
        byte[] pdfBytes = pdfService.generateInvoicePdf(invoice);

        // 3. Configuramos las cabeceras para que el navegador sepa que es un PDF
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("factura-" + invoice.getInvoiceNumber() + ".pdf").build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/billCards/{date}")
    public ResponseEntity<BillCardsData> getBillCards(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        BigDecimal incomes = invoiceService.incomesPerDay(date);
        Integer emittedBills = invoiceService.countByDate(date);
        BillCardsData billCardsData = new BillCardsData();
        billCardsData.setEmittedBills(emittedBills);
        billCardsData.setIncomesPerDay(incomes);
        return ResponseEntity.ok(billCardsData);
    }

    @GetMapping("/report/monthly")
    public ResponseEntity<byte[]> getMonthlyReport(
            @RequestParam int month,
            @RequestParam int year) {

        byte[] pdfContent = pdfService.generateMonthlyReport(month, year);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // attachment para que se descargue, inline para que se abra en el navegador
        headers.setContentDispositionFormData("filename", "informe-" + month + "-" + year + ".pdf");

        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

    @GetMapping("/from-appointment/{id}/export-pdf")
    public ResponseEntity<byte[]> getInvoiceFromAppointment(@PathVariable Long id) {
        Invoice invoice = invoiceService.findByAppointmentId(id);
        byte[] pdfContent = pdfService.generateInvoicePdf(invoice);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "informe-" + id + ".pdf");
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
}