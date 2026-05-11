package com.mby.myStore.DTO;

import com.mby.myStore.Model.paymentMethod;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
public class InvoiceResponse {
    private Long id;

    private String invoiceNumber;

    private String clientName;

    private String employeeName;

    private LocalDate date;

    private paymentMethod paymentMethod;

    private BigDecimal total;
}
