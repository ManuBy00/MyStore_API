package com.mby.myStore.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.*;

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
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    @Schema(description = "Nombre completo del empleado", example = "Carlos Rodríguez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;


    @Column(name = "hire_date")
    @Schema(description = "Fecha de alta del empleado", example = "2025-08-12")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;

    @ColumnDefault("1")
    @Column(name = "active")
    @Schema(description = "Indica si el empleado está actualmente en activo", example = "true")
    private Boolean active;

    @OneToMany
    @JoinColumn(name = "employee_id")
    @JsonIgnore
    @Schema(description = "Listado de citas asociadas al empleado", hidden = true)
    private Set<Appointment> appointments = new LinkedHashSet<>();


    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("employee") //Para evitar bucles infinitos en el JSON
    private List<Absence> absences = new ArrayList<>();

}