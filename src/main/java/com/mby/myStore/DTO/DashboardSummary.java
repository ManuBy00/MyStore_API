package com.mby.myStore.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DashboardSummary {
    List<ServiceCountDTO> serviceCountDTO;
    int employeesNumber;
    int productsNumber;
}
