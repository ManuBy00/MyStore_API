package com.mby.myStore.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Schema(description = "Datos públicos del cliente (sin información sensible)")
public class ClienteDTO {

    private int id;
    private String nombre;
    private String email;

    @Schema(description = "Fecha en la que el cliente se unió a la plataforma")
    private Instant fechaRegistro;
}