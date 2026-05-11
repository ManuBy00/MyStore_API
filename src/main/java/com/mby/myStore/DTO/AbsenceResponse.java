package com.mby.myStore.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AbsenceResponse {
    private Long id;
    private Long employeeId;    // Solo el ID
    private String employeeName; // Opcional, para el mensaje de éxito
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    // Getters y Setters
}