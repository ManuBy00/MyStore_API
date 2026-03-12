package com.mby.myStore.Repositories;

import com.mby.myStore.Model.Empleado;
import com.mby.myStore.Model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio,Integer> {

    Servicio getServiciosById(Integer id);
}
