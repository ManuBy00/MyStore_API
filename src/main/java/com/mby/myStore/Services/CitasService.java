package com.mby.myStore.Services;


import com.mby.myStore.DTO.CitaDTO;
import com.mby.myStore.Exceptions.RecordNotFoundException;
import com.mby.myStore.Exceptions.SlotAlreadyOccupiedException;
import com.mby.myStore.Model.Cita;
import com.mby.myStore.Model.Cliente;
import com.mby.myStore.Model.Empleado;
import com.mby.myStore.Model.Servicio;
import com.mby.myStore.Repositories.CitaRepository;
import com.mby.myStore.Repositories.ClienteRepository;
import com.mby.myStore.Repositories.EmpleadoRepository;
import com.mby.myStore.Repositories.ServicioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
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
     * @param dto Objeto con los datos de la reserva (Fecha, Hora Inicio, IDs de relaciones).
     * @throws SlotAlreadyOccupiedException Si el horario ya está comprometido para ese empleado.
     */
    public CitaDTO createCita(CitaDTO dto) throws SlotAlreadyOccupiedException {
        // 1. Carga de dependencias con mensajes de error claros
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RecordNotFoundException("Cliente ID " + dto.getClienteId() + " no encontrado"));
        Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new RecordNotFoundException("Empleado ID " + dto.getEmpleadoId() + " no encontrado"));
        Servicio servicio = servicioRepository.findById(dto.getServicioId())
                .orElseThrow(() -> new RecordNotFoundException("Servicio ID " + dto.getServicioId() + " no encontrado"));

        // 2. Construcción del objeto (Mapeo)
        Cita cita = new Cita();
        cita.setFecha(dto.getFecha());
        cita.setHoraInicio(dto.getHoraInicio());
        cita.setCliente(cliente);
        cita.setEmpleado(empleado);
        cita.setServicio(servicio);

        // Valores automáticos
        cita.setCreatedAt(Instant.now());
        cita.setEstado("Confirmada");

        // Cálculo de fin basado en la duración del servicio recuperado
        cita.setHoraFin(dto.getHoraInicio().plusMinutes(servicio.getDuracionMinutos()));

        // 3. Validación de Negocio: Se hace justo antes de guardar
        if (!checkDispo(cita)) {
            throw new SlotAlreadyOccupiedException("El horario solicitado (" +
                    cita.getHoraInicio() + " - " + cita.getHoraFin() + ") ya está ocupado para " + empleado.getNombre());
        }

        citaRepository.save(cita);
        return entityToDTO(cita);
    }

    /**
     * Elimina una cita existente tras verificar su presencia en la base de datos.
     */
    public void deleteCita(int id) {
        if (citaRepository.existsById(id)){
            citaRepository.deleteById(id);
        }else {
            throw new RecordNotFoundException("La cita no existe");
        }
    }

    /**
     * Actualiza una cita permitiendo cambiar fecha, hora, empleado o servicio.
     * @param id Identificador de la cita original.
     * @param citaEditada Objeto con los nuevos valores.
     */
    @Transactional
    public Cita updateCita(int id, CitaDTO citaEditada) throws SlotAlreadyOccupiedException {
        // Verificamos que la cita existe
        Cita citaExistente = citaRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("No se encontró la cita con ID: " + id));

        // recuperamos el servicio para recalcular la hora fin (por si ha cambiado el tipo de corte)
        Servicio servicio = servicioRepository.findById(citaEditada.getServicioId())
                .orElseThrow(() -> new RecordNotFoundException("El servicio con ID " + citaEditada.getServicioId() + " no existe"));


        Empleado empleado = empleadoRepository.findById(citaEditada.getEmpleadoId())
                .orElseThrow(() -> new RecordNotFoundException("El empleado con ID " + citaEditada.getEmpleadoId() + " no existe"));

        LocalTime nuevaHoraFin = citaEditada.getHoraInicio().plusMinutes(servicio.getDuracionMinutos());

        //  VALIDACIÓN DE SOLAPAMIENTOS POR SI SE CAMBIA LA HORA O DÍA
        // Buscamos conflictos para el nuevo horario/empleado
        List<Cita> conflictos = citaRepository.comprobarDispo(
                empleado,
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
        citaExistente.setEmpleado(empleado);
        citaExistente.setServicio(servicio);

        citaRepository.save(citaExistente);
        return citaExistente;
    }


    public List<CitaDTO> getCitasByFecha(LocalDate fecha) {
        return citaRepository.getCitasByFecha(fecha).stream().map(this::entityToDTO).toList();
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
    public List<CitaDTO> buscarCitasPorNombreCliente(String nombre) {
        // Llamamos al método con la @Query personalizada que creamos en el Repository
        return citaRepository.findByNombreClientePersonalizado(nombre).stream().map(this::entityToDTO).toList();
    }

    /**
     * Convierte una entidad Cita en un objeto CitaDTO.
     * Extrae solo los IDs de las relaciones para simplificar la respuesta.
     */
    public CitaDTO entityToDTO(Cita cita) {
        CitaDTO dto = new CitaDTO();

        // Copiamos los datos básicos
        dto.setFecha(cita.getFecha());
        dto.setHoraInicio(cita.getHoraInicio());

        // Extraemos los IDs de los objetos relacionados
        dto.setClienteId(cita.getCliente().getId());
        dto.setEmpleadoId(cita.getEmpleado().getId());
        dto.setServicioId(cita.getServicio().getId());

        return dto;
    }
}
