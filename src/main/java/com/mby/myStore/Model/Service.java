package com.mby.myStore.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "services")
@Schema(description = "Modelo que representa los servicios ofrecidos en la tienda (ej. Corte, Tinte, etc.)")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Schema(description = "Identificador único del servicio", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    @Schema(description = "Nombre descriptivo del servicio", example = "Corte de pelo Caballero", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull
    @Column(name = "price", nullable = false)
    @Schema(description = "Costo del servicio en la moneda local", example = "15.50", requiredMode = Schema.RequiredMode.REQUIRED)
    private double price;

    @NotNull
    @Column(name = "duration_minutes", nullable = false)
    @Schema(description = "Duración estimada del servicio en minutos", example = "30", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long durationMinutes;

    @OneToMany
    @JoinColumn(name = "service_id")
    @JsonIgnore
    @Schema(description = "Relación de citas que incluyen este servicio", hidden = true)
    private Set<Appointment> appointments = new LinkedHashSet<>();

}