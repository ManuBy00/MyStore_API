package com.mby.myStore.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "business_shifts")
@Getter
@Setter
public class BusinessShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek; // Enum: MONDAY, TUESDAY...

    private LocalTime startTime; // Ejemplo: 08:00
    private LocalTime endTime;   // Ejemplo: 14:00
}