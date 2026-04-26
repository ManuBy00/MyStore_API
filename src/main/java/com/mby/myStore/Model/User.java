package com.mby.myStore.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "user")
@Schema(description = "Modelo que representa a un usuario o cliente del sistema")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Schema(description = "ID único del usuario", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 150)
    @NotNull
    @Column(name = "email", nullable = false, length = 150)
    @Schema(description = "Correo electrónico para inicio de sesión y contacto", example = "juan.perez@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    @Schema(description = "Contraseña encriptada del usuario", example = "********", requiredMode = Schema.RequiredMode.REQUIRED, format = "password")
    private String password;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "register_date")
    @Schema(description = "Fecha y hora en la que el usuario se registró", example = "2024-01-01T12:00:00Z", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant registerDate;

    @Column(name = "telephone")
    @Schema(description = "Número de teléfono del usuario", example = "642508200")
    private String telNumber;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Rol asignado al usuario dentro de la plataforma", example = "CLIENTE", allowableValues = {"ADMIN", "CLIENTE", "EMPLEADO"})
    private Role role;

    @OneToMany
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    @Schema(description = "Historial de citas solicitadas por el usuario", hidden = true)
    private Set<Appointment> appointments = new LinkedHashSet<>();

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.registerDate = Instant.now();
    }

    public User() {
    }
}