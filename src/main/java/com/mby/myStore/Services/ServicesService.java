package com.mby.myStore.Services;

import com.mby.myStore.Model.Service;
import com.mby.myStore.Repositories.ServiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
@Transactional
public class ServicesService {
    @Autowired
    ServiceRepository serviceRepository;

    public List<Service> getServicios(){
        if (serviceRepository.findAll().isEmpty()) {
            return new ArrayList<>();
        }
        return serviceRepository.findAll();
    }






}
