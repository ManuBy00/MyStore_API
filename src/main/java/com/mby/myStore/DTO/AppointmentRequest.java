package com.mby.myStore.DTO;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "DTO para crear o actualizar una cita. Es lo que requiere la api para operaciones de escritura")
public class AppointmentRequest {

    //DTO que recibe la api para operaciones de creación o edición

    private int id;

    @Schema(example = "2026-05-15")
    private LocalDate date;

    @Schema(type = "string", pattern = "HH:mm:ss", example = "10:30:00")
    private LocalTime startTime;

    @Schema(type = "string", example = "642506254")
    private String telNumber;

    @Schema(description = "ID del cliente", example = "1")
    private int clientId;

    @Schema(description = "ID del barbero", example = "1")
    private int employeeId;

    @Schema(description = "ID del servicio", example = "2")
    private int serviceId;
}