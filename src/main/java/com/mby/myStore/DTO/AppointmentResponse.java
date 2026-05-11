package com.mby.myStore.DTO;

import com.mby.myStore.Model.AppoStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO optimizado para mostrar la información de una cita en la interfaz de usuario (Cards/Listas). En lugar de mostrar el id muestra el nombre de las entidades relacionadas")
public class AppointmentResponse {

    @Schema(description = "ID de la cita", example = "101", accessMode =  Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Fecha de la cita", example = "2024-12-20")
    private LocalDate date;

    @Schema(description = "Hora de inicio", example = "16:30")
    private LocalTime startTime;

    // Datos para la UI (Card)
    @Schema(description = "Nombre completo del cliente", example = "Ana García")
    private String customerName;

    private Long customerId;

    @Schema(description = "Número de teléfono de cliente")
    private String telNumber;

    @Schema(description = "Nombre del servicio contratado", example = "Manicura Premium")
    private String serviceName;

    @Schema(description = "ID del servicio contratado", example = "1")
    private Long serviceId;

    @Schema(description = "Nombre del empleado que atenderá", example = "Elena Soler")
    private String employeeName;

    @Schema(description = "ID del barbero", example = "1")
    private Long employeeId;

    @Schema(description = "Duración total en minutos", example = "45")
    private Long durationMinutes;

    @Schema(description = "Precio final del servicio", example = "25.00")
    private Double price;

    @Schema(description = "Estado de la cita")
    private AppoStatus status;
}