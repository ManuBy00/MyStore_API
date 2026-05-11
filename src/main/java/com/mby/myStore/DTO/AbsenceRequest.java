package com.mby.myStore.DTO;

import com.mby.myStore.Model.Employee;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AbsenceRequest {

    private Long employeeId;

    private LocalDate startDate;

    private LocalDate endDate;

    private String reason;
}
