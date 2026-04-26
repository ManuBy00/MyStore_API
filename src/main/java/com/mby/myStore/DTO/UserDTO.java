package com.mby.myStore.DTO;

import com.mby.myStore.Model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Data;

import java.time.Instant;

@Data
@Schema(description = "DTO para la representación de datos de usuario en respuestas de la API (excluye credenciales)")
public class UserDTO {


    @Schema(description = "Identificador único del usuario", example = "42")
    private int id;

    @Schema(description = "Nombre completo del usuario", example = "Marina Berrio")
    private String name;

    @Schema(description = "Dirección de correo electrónico", example = "marina@mystore.com")
    private String email;


    @Schema(description = "Número de teléfono del usuario", example = "642508200")
    private String telNumber;

    @Schema(description = "Rol o privilegios asignados", example = "CLIENTE")
    private Role role;

    @Schema(description = "Fecha y hora en la que el cliente se unió a la plataforma", example = "2024-03-10T15:30:00Z")
    private Instant registerDate;

}