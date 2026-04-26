package com.mby.myStore.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ServiceCountDTO {
    private String serviceName;
    private Long count;

    public ServiceCountDTO(String serviceName, Long count) {
        this.serviceName = serviceName;
        this.count = count;
    }
}

