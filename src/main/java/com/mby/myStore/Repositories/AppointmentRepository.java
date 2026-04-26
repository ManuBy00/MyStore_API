package com.mby.myStore.Repositories;

import com.mby.myStore.DTO.ServiceCountDTO;
import com.mby.myStore.Model.Appointment;
import com.mby.myStore.Model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Integer> {

    /**
     * Consulta personalizada para detectar solapamientos de horario (Conflictos).
     * Utiliza lógica de intervalos para verificar si una nueva cita se pisa con una existente.
     *
     * @return Lista de citas que colisionan con el rango horario proporcionado.
     */
    @Query("SELECT c " +
            "FROM Appointment c " +
            "WHERE c.date = :fecha " +
            "AND c.employee = :emp " +
            "AND (:horaInicio < c.endTime AND :horaFin > c.startTime) ")
    List<Appointment> checkAvailability(@Param("emp") Employee emp,
                                        @Param("horaInicio") LocalTime horaInicio,
                                        @Param("horaFin") LocalTime horaFin,
                                        @Param("fecha") LocalDate fecha);

    /**
     * Query Method automático de Spring Data JPA.
     * Recupera todas las citas registradas en una fecha específica.
     */
    List<Appointment> getAppointmentsByDate(LocalDate fecha);

    /**
     * Query Method que filtra por la relación con Empleado y una fecha.
     * Es la base para el algoritmo que calcula los huecos libres de un barbero.
     */
    List<Appointment> findByEmployeeIdAndDate(int empleadoId, LocalDate fecha);

    /**
     * Consulta compleja con JOIN manual.
     * Navega desde la entidad Cita hasta la entidad Cliente para filtrar por un
     * atributo que no pertenece a la tabla raíz (el nombre del cliente).
     *
     * @param nombre Cadena de texto para búsqueda parcial (LIKE).
     */
    @Query("SELECT c FROM Appointment c JOIN c.user cl WHERE cl.name LIKE %:nombre%")
    List<Appointment> findByUserName(@Param("nombre") String nombre);


    @Query("SELECT new com.mby.myStore.DTO.ServiceCountDTO(s.name, count(a)) " +
            "FROM Appointment a JOIN a.service s " +
            "WHERE a.date = :today " +
            "GROUP BY s.name")
    List<ServiceCountDTO> countServicesPerDay(@Param("today") LocalDate today);
}
