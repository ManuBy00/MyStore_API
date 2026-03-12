package com.mby.myStore.Services;


import com.mby.myStore.Exceptions.RecordNotFoundException;
import com.mby.myStore.Exceptions.SlotAlreadyOccupiedException;
import com.mby.myStore.Model.Cita;
import com.mby.myStore.Model.Servicio;
import com.mby.myStore.Repositories.CitaRepository;
import com.mby.myStore.Repositories.ClienteRepository;
import com.mby.myStore.Repositories.EmpleadoRepository;
import com.mby.myStore.Repositories.ServicioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CitasService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    public void addCita(Cita cita) throws SlotAlreadyOccupiedException {
        //establecemos la hora de final dependiendo del servicio
        Servicio servicio = servicioRepository.getServiciosById(cita.getServicio().getId());
        LocalTime horaFin = cita.getHoraInicio().plusMinutes(servicio.getDuracionMinutos());
        cita.setHoraFin(horaFin);

        if (citaRepository.comprobarDispo(cita.getEmpleado(), cita.getHoraInicio(), cita.getHoraFin(), cita.getFecha()).isEmpty()) {
            citaRepository.save(cita);
        }else {
            throw new SlotAlreadyOccupiedException("El empleado ya tiene una cita reservada que se solapa con este horario.");
        }
    }

    public void deleteCita(Cita cita) {
        if (citaRepository.existsById(cita.getId())) {
            citaRepository.delete(cita);
        }else {
            throw new RecordNotFoundException("El empleado no existe");
        }
    }

    @Transactional
    public void updateCita(int id, Cita citaEditada) throws SlotAlreadyOccupiedException {
        // 1. Verificamos que la cita existe
        Cita citaExistente = citaRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("No se encontró la cita con ID: " + id));

        // 2. recuperamos el servicio para recalcular la hora fin (por si ha cambiado el tipo de corte)
        Servicio servicio = servicioRepository.findById(citaEditada.getServicio().getId()).get();

        LocalTime nuevaHoraFin = citaEditada.getHoraInicio().plusMinutes(servicio.getDuracionMinutos());

        // 3. VALIDACIÓN DE SOLAPAMIENTOS
        // Buscamos conflictos para el nuevo horario/empleado
        List<Cita> conflictos = citaRepository.comprobarDispo(
                citaEditada.getEmpleado(),
                citaEditada.getHoraInicio(),
                nuevaHoraFin,
                citaEditada.getFecha()
        );

        // 4. FILTRARSE A SÍ MISMO
        // Si hay conflictos, debemos comprobar que no sean la propia cita que estamos editando
        boolean hayChoqueReal = conflictos.stream()
                .anyMatch(c -> c.getId() != id);

        if (hayChoqueReal) {
            throw new SlotAlreadyOccupiedException("El nuevo horario se solapa con otra cita existente.");
        }

        // 5. Actualizamos los datos
        citaExistente.setFecha(citaEditada.getFecha());
        citaExistente.setHoraInicio(citaEditada.getHoraInicio());
        citaExistente.setHoraFin(nuevaHoraFin);
        citaExistente.setEmpleado(citaEditada.getEmpleado());
        citaExistente.setServicio(servicio);

        citaRepository.save(citaExistente);
    }

    public List<Cita> getCitasByFecha(LocalDate fecha) {
        return citaRepository.getCitasByFecha(fecha);
    }


    boolean checkDispo(Cita cita) {
        if(citaRepository.comprobarDispo(cita.getEmpleado(), cita.getHoraInicio(), cita.getHoraFin(), cita.getFecha()).isEmpty()) {
            return true;
        }else {
            return false;
        }
    }

    public List<LocalTime> obtenerHorasLibres(int empleadoId, LocalDate fecha) {
        // 1. Definimos los tramos que queremos mostrar en la App
        List<LocalTime> todosLosTramos = List.of(
                LocalTime.of(16, 0), LocalTime.of(16, 30),
                LocalTime.of(17, 0), LocalTime.of(17, 30),
                LocalTime.of(18, 0), LocalTime.of(18, 30),
                LocalTime.of(19, 0), LocalTime.of(19, 30),
                LocalTime.of(20, 0), LocalTime.of(20, 30)

        );

        // 2. UNA SOLA CONSULTA: Traemos todas las citas de ese día para ese barbero
        List<Cita> citasDelDia = citaRepository.findByEmpleadoIdAndFecha(empleadoId, fecha);

        return todosLosTramos.stream()
                .filter(tramo -> estaLibre(tramo, citasDelDia))
                .collect(Collectors.toList());
    }

    private boolean estaLibre(LocalTime tramo, List<Cita> citas) {
        for (Cita cita : citas) {
            // Si el tramo cae dentro de una cita, no está libre
            if (!tramo.isBefore(cita.getHoraInicio()) && tramo.isBefore(cita.getHoraFin())) {
                return false;
            }
        }
        return true; // Si recorre todas y no choca, está libre
    }
}
