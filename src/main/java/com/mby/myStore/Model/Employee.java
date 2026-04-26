package com.mby.myStore.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "employee")
@Schema(description = "Modelo que representa a un empleado del establecimiento")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Schema(description = "Identificador único del empleado", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    @Schema(description = "Nombre completo del empleado", example = "Carlos Rodríguez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 50)
    @Column(name = "speciality", length = 50)
    @Schema(description = "Especialidad técnica del empleado", example = "Corte de cabello y Barba")
    private String speciality;

    @ColumnDefault("1")
    @Column(name = "active")
    @Schema(description = "Indica si el empleado está actualmente en activo", example = "true")
    private Boolean active;

    @OneToMany
    @JoinColumn(name = "employee_id")
    @JsonIgnore
    @Schema(description = "Listado de citas asociadas al empleado", hidden = true)
    private Set<Appointment> appointments = new LinkedHashSet<>();

}