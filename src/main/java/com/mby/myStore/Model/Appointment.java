package com.mby.myStore.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "Appointment")
@Schema(description = "Modelo que representa una cita en el sistema")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Schema(description = "Identificador único de la cita", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    @NotNull
    @Column(name = "date", nullable = false)
    @Schema(description = "Fecha programada para la cita", example = "2024-12-15", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate date;

    @Size(max = 20)
    @ColumnDefault("'PENDIENTE'")
    @Column(name = "status", length = 20)
    @Schema(description = "Estado actual de la cita", example = "PENDIENTE", allowableValues = {"PENDIENTE", "CONFIRMADA", "CANCELADA"})
    private String status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "customer_id", nullable = false)
    @Schema(description = "Usuario que solicita la cita (Cliente)")
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    @Schema(description = "Empleado asignado para atender la cita")
    private Employee employee;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    @Schema(description = "Servicio que se realizará durante la cita")
    private Service service;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    @Schema(description = "Fecha y hora de creación del registro", example = "2024-05-20T10:00:00Z", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;

    @Column(name = "start_time")
    @Schema(description = "Hora de inicio de la cita", example = "10:30:00")
    private LocalTime startTime;

    @Column(name = "end_time")
    @Schema(description = "Hora estimada de finalización", example = "11:30:00")
    private LocalTime endTime;

}