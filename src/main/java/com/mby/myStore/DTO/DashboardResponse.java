package com.mby.myStore.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DashboardResponse {
    List<ServiceCountDTO> serviceCountDTO;
    Long employeesNumber;
    int availableSlots;
}
