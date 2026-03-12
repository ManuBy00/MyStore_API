package com.mby.myStore.Services;

import com.mby.myStore.Model.Servicio;
import com.mby.myStore.Repositories.ServicioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ServiciosService {
    @Autowired
    ServicioRepository servicioRepository;

    public List<Servicio> getServicios(){
        if (servicioRepository.findAll().isEmpty()) {
            return new ArrayList<>();
        }
        return servicioRepository.findAll();
    }




}
