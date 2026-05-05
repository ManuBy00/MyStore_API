package com.mby.myStore.Services;

import com.mby.myStore.DTO.InvoiceRequest;
import com.mby.myStore.DTO.InvoiceResponse;
import com.mby.myStore.Model.Invoice;
import com.mby.myStore.Repositories.AppointmentRepository;
import com.mby.myStore.Repositories.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoicesService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public InvoiceResponse createInvoice(InvoiceRequest invoice) {
        if (appointmentRepository.existsById(invoice.getAppointmentId())){

        }
    }

    public InvoiceResponse toResponse(Invoice entity) {
        InvoiceResponse dto = new InvoiceResponse();
        dto.setId(entity.getId());
        dto.setInvoiceNumber(entity.getInvoiceNumber());
        dto.setDate(entity.getDate());
        dto.setTotal(entity.getTotal());
        dto.setPaymentMethod(entity.getPaymentMethod());

        // Navegamos por la relación que tú definiste
        if (entity.getAppointment() != null) {
            dto.setClientName(entity.getAppointment().getUser().getName());
            dto.setEmployee(entity.getAppointment().getEmployee().getName());
        }
        return dto;
    }
}
