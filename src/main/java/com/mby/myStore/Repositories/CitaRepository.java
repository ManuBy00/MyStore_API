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

    /**
     * Consulta personalizada para detectar solapamientos de horario (Conflictos).
     * Utiliza lógica de intervalos para verificar si una nueva cita se pisa con una existente.
     * @return Lista de citas que colisionan con el rango horario proporcionado.
     */
    @Query("SELECT c " +
            "FROM Cita c " +
            "WHERE c.fecha = :fecha " +
            "AND c.empleado = :emp " +
            "AND (:horaInicio < c.horaFin AND :horaFin > c.horaInicio) ")
    List<Cita> comprobarDispo(@Param("emp") Empleado emp,
                              @Param("horaInicio")LocalTime horaInicio,
                              @Param("horaFin")LocalTime horaFin,
                              @Param("fecha") LocalDate fecha);

    /**
     * Query Method automático de Spring Data JPA.
     * Recupera todas las citas registradas en una fecha específica.
     */
    List<Cita> getCitasByFecha(LocalDate fecha);

    /**
     * Query Method que filtra por la relación con Empleado y una fecha.
     * Es la base para el algoritmo que calcula los huecos libres de un barbero.
     */
    List<Cita>findByEmpleadoIdAndFecha(int empleadoId, LocalDate fecha);

    /**
     * Consulta compleja con JOIN manual.
     * Navega desde la entidad Cita hasta la entidad Cliente para filtrar por un
     * atributo que no pertenece a la tabla raíz (el nombre del cliente).
     * @param nombre Cadena de texto para búsqueda parcial (LIKE).
     */
    @Query("SELECT c FROM Cita c JOIN c.cliente cl WHERE cl.nombre LIKE %:nombre%")
    List<Cita> findByNombreClientePersonalizado(@Param("nombre") String nombre);
    }
