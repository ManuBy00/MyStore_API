package com.mby.myStore.Repositories;

import com.mby.myStore.Model.Cita;
import com.mby.myStore.Model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita,Integer> {

    @Query("SELECT c " +
            "FROM Cita c " +
            "WHERE c.fecha = :fecha " +
            "AND c.empleado = :emp " +
            "AND (:horaInicio < c.horaFin AND :horaFin > c.horaInicio) ")
    List<Cita> comprobarDispo(@Param("emp") Empleado emp,
                              @Param("horaInicio")LocalTime horaInicio,
                              @Param("horaFin")LocalTime horaFin,
                              @Param("fecha") LocalDate fecha);

    List<Cita> getCitasByFecha(LocalDate fecha);

    List<Cita>findByEmpleadoIdAndFecha(int empleadoId, LocalDate fecha);

    }
