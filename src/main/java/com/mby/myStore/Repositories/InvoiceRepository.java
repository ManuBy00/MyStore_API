package com.mby.myStore.Repositories;

import com.mby.myStore.Model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice,Long> {

    Optional<Invoice> findByAppointmentId(Long appointmentId);


    // Para buscar por número de factura (negocio)
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByDate(LocalDate date);

    Integer countByDate(LocalDate date);

    List<Invoice> findByDateBetween(LocalDate startDate, LocalDate endDate);



}
