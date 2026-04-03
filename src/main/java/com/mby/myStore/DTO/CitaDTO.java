package com.mby.myStore.DTO;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "Modelo simplificado para crear o actualizar una cita")
public class CitaDTO {

    @Schema(example = "2026-05-15")
    private LocalDate fecha;

    @Schema(type = "string", pattern = "HH:mm:ss", example = "10:30:00")
    private LocalTime horaInicio;

    @Schema(description = "ID del cliente", example = "1")
    private int clienteId;

    @Schema(description = "ID del barbero", example = "1")
    private int empleadoId;

    @Schema(description = "ID del servicio", example = "2")
    private int servicioId;
}