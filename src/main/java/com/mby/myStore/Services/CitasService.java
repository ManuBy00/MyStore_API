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

    /**
     * Registra una nueva cita calculando automáticamente la duración y validando disponibilidad.
     * @param cita Objeto con los datos de la reserva (Fecha, Hora Inicio, IDs de relaciones).
     * @throws SlotAlreadyOccupiedException Si el horario ya está comprometido para ese empleado.
     */
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

    /**
     * Elimina una cita existente tras verificar su presencia en la base de datos.
     */
    public void deleteCita(Cita cita) {
        if (citaRepository.existsById(cita.getId())) {
            citaRepository.delete(cita);
        }else {
            throw new RecordNotFoundException("El empleado no existe");
        }
    }

    /**
     * Actualiza una cita permitiendo cambiar fecha, hora, empleado o servicio.
     * @param id Identificador de la cita original.
     * @param citaEditada Objeto con los nuevos valores.
     */
    @Transactional
    public void updateCita(int id, Cita citaEditada) throws SlotAlreadyOccupiedException {
        // Verificamos que la cita existe
        Cita citaExistente = citaRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("No se encontró la cita con ID: " + id));

        // recuperamos el servicio para recalcular la hora fin (por si ha cambiado el tipo de corte)
        Servicio servicio = servicioRepository.findById(citaEditada.getServicio().getId()).get();

        LocalTime nuevaHoraFin = citaEditada.getHoraInicio().plusMinutes(servicio.getDuracionMinutos());

        //  VALIDACIÓN DE SOLAPAMIENTOS
        // Buscamos conflictos para el nuevo horario/empleado
        List<Cita> conflictos = citaRepository.comprobarDispo(
                citaEditada.getEmpleado(),
                citaEditada.getHoraInicio(),
                nuevaHoraFin,
                citaEditada.getFecha()
        );

        // FILTRARSE A SÍ MISMO
        // Si hay conflictos, debemos comprobar que no sean la propia cita que estamos editando
        boolean hayChoqueReal = conflictos.stream()
                .anyMatch(c -> c.getId() != id);

        if (hayChoqueReal) {
            throw new SlotAlreadyOccupiedException("El nuevo horario se solapa con otra cita existente.");
        }

        // Actualizamos los datos
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

    /**
     * Algoritmo principal para mostrar la agenda disponible en la App Android.
     * Genera tramos horarios y los filtra comparándolos con las citas reales del día.
     */
    public List<LocalTime> obtenerHorasLibres(int empleadoId, LocalDate fecha) {
        // Definimos los tramos que queremos mostrar en la App
        List<LocalTime> todosLosTramos = List.of(
                LocalTime.of(16, 0), LocalTime.of(16, 30),
                LocalTime.of(17, 0), LocalTime.of(17, 30),
                LocalTime.of(18, 0), LocalTime.of(18, 30),
                LocalTime.of(19, 0), LocalTime.of(19, 30),
                LocalTime.of(20, 0), LocalTime.of(20, 30)

        );

        //  UNA SOLA CONSULTA: Traemos todas las citas de ese día para ese barbero
        List<Cita> citasDelDia = citaRepository.findByEmpleadoIdAndFecha(empleadoId, fecha);

        return todosLosTramos.stream()
                .filter(tramo -> estaLibre(tramo, citasDelDia))
                .collect(Collectors.toList());
    }

    /**
     * Función auxiliar que determina si un punto específico en el tiempo (tramo)
     * cae dentro del intervalo [Inicio, Fin) de alguna cita ocupada.
     */
    private boolean estaLibre(LocalTime tramo, List<Cita> citas) {
        for (Cita cita : citas) {
            // Si el tramo cae dentro de una cita, no está libre
            if (!tramo.isBefore(cita.getHoraInicio()) && tramo.isBefore(cita.getHoraFin())) {
                return false;
            }
        }
        return true; // Si recorre todas y no choca, está libre
    }


    /**
     * Lógica de negocio para buscar citas por el nombre de un cliente.
     * @param nombre Cadena de texto a buscar (nombre del cliente).
     * @return Lista de citas que coinciden con el criterio.
     */
    public List<Cita> buscarCitasPorNombreCliente(String nombre) {
        // Llamamos al método con la @Query personalizada que creamos en el Repository
        return citaRepository.findByNombreClientePersonalizado(nombre);
    }
}
