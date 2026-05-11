package com.mby.myStore.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "absences")
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER) // Trae al empleado siempre que consultes la ausencia
    @JoinColumn(name = "employee_id", nullable = false) // Crea la FK "employee_id" y no permite nulos
    @NotNull(message = "El empleado es obligatorio")
    private Employee employee;

    @Column(name = "start_date", nullable = false)
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate endDate;

    @Column(length = 500) // Un poco más de espacio por si el motivo es largo
    private String reason;
}