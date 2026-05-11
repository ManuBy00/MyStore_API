package com.mby.myStore.DTO;

import com.mby.myStore.Model.paymentMethod;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceRequest {
    // Solo necesitamos el ID de la cita para vincularlos
    private Long appointmentId;

    // Estos datos los decide el usuario en el momento de cobrar
    private paymentMethod paymentMethod;

    // El total podría venir calculado del front o recalcularse en el back
    private BigDecimal total;

    // La fecha y el número de facturase generan en el servidor
}