package com.mby.myStore.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoices")
@Getter
@Setter
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", nullable = false, unique = true)
    private String invoiceNumber;

    @Column(nullable = false)
    private LocalDate date;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private paymentMethod paymentMethod;

    // DATOS SNAPSHOT (Estos se llenan al crear la factura y NO cambian nunca)
    @Column(name = "client_name_snapshot", nullable = false, length = 150)
    private String clientName;
    @Column(name = "employee_name_snapshot", nullable = false, length = 150)
    private String employeeName;   // Por si el empleado se va de la empresa
    @Column(name = "service_name_snapshot", nullable = false, length = 150)
    private String serviceName;    // Por si el servicio cambia de "Corte" a "Corte Premium"
    @Column(name = "price_snapshot", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;      // El precio que pagó ESE día

}