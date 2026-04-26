package com.mby.myStore.Services;


import com.mby.myStore.DTO.AppointmentDTO;
import com.mby.myStore.DTO.AppointmentViewDTO;
import com.mby.myStore.DTO.ServiceCountDTO;
import com.mby.myStore.Exceptions.DateNotValidException;
import com.mby.myStore.Exceptions.RecordNotFoundException;
import com.mby.myStore.Exceptions.SlotAlreadyOccupiedException;
import com.mby.myStore.Model.Appointment;
import com.mby.myStore.Model.User;
import com.mby.myStore.Model.Employee;
import com.mby.myStore.Model.Service;
import com.mby.myStore.Repositories.AppointmentRepository;
import com.mby.myStore.Repositories.UserRepository;
import com.mby.myStore.Repositories.EmployeeRepository;
import com.mby.myStore.Repositories.ServiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@Transactional
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Registra una nueva cita calculando automáticamente la duración y validando disponibilidad. Si es fin de semana
     * @param dto Objeto con los datos de la reserva (Fecha, Hora Inicio, IDs de relaciones).
     * @throws SlotAlreadyOccupiedException Si el horario ya está comprometido para ese empleado.
     */
    public AppointmentDTO createAppointment(AppointmentDTO dto) throws SlotAlreadyOccupiedException {
        //  Carga de dependencias con mensajes de error
        User user = userRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RecordNotFoundException("Cliente ID " + dto.getClientId() + " no encontrado"));
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RecordNotFoundException("Empleado ID " + dto.getEmployeeId() + " no encontrado"));
        Service service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new RecordNotFoundException("Servicio ID " + dto.getServiceId() + " no encontrado"));

        //validamos que la fecha no sea anterior a hoy
        if (dto.getDate().isBefore(LocalDate.now())) {
            throw new DateNotValidException("No se pueden programar citas en fechas pasadas.");
        }

        // Validar que no sea Fin de Semana
        DayOfWeek day = dto.getDate().getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            throw new DateNotValidException("No abrimos los fines de semana. Por favor, elige un día de lunes a viernes.");
        }

        //  Construcción del objeto (Mapeo)
        Appointment appointment = new Appointment();
        appointment.setDate(dto.getDate());
        appointment.setStartTime(dto.getStartTime());
        appointment.setUser(user);
        appointment.setEmployee(employee);
        appointment.setService(service);

        // Valores automáticos
        appointment.setCreatedAt(Instant.now());
        appointment.setStatus("Confirmada");

        // Cálculo de fin basado en la duración del servicio recuperado
        appointment.setEndTime(dto.getStartTime().plusMinutes(service.getDurationMinutes()));

        //  Validación de Negocio: Se hace justo antes de guardar
        if (!checkAvailability(appointment)) {
            throw new SlotAlreadyOccupiedException("El horario solicitado (" +
                    appointment.getStartTime() + " - " + appointment.getEndTime() + ") ya está ocupado para " + employee.getName());
        }

        appointmentRepository.save(appointment);
        return mapToDTO(appointment);
    }

    /**
     * Elimina una cita existente tras verificar su presencia en la base de datos.
     */
    public void deleteAppointment(int id) {
        if (appointmentRepository.existsById(id)){
            appointmentRepository.deleteById(id);
        }else {
            throw new RecordNotFoundException("La cita introducida no existe");
        }
    }

    /**
     * Actualiza una cita permitiendo cambiar fecha, hora, empleado o servicio.
     * @param id Identificador de la cita original.
     * @param citaEditada Objeto con los nuevos valores.
     */
    @Transactional
    public AppointmentDTO updateAppointment(int id, AppointmentDTO citaEditada) throws SlotAlreadyOccupiedException {
        // Verificamos que la cita existe
        Appointment appointmentExistente = appointmentRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("No se encontró la cita con ID: " + id));

        //  Cargamos las nuevas dependencias (Empleado y Servicio)
        Service service = serviceRepository.findById(citaEditada.getServiceId())
                .orElseThrow(() -> new RecordNotFoundException("Servicio no encontrado"));
        Employee employee = employeeRepository.findById(citaEditada.getEmployeeId())
                .orElseThrow(() -> new RecordNotFoundException("Empleado no encontrado"));

        // Calculamos la nueva hora de fin
        LocalTime nuevaHoraFin = citaEditada.getStartTime().plusMinutes(service.getDurationMinutes());

        //validación de fecha
        if (citaEditada.getDate().isBefore(LocalDate.now())) {
            throw new DateNotValidException("No se pueden programar citas en fechas pasadas.");
        }

        // Validar que no sea Fin de Semana
        DayOfWeek dia = citaEditada.getDate().getDayOfWeek();
        if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY) {
            throw new DateNotValidException("No abrimos los fines de semana. Por favor, elige un día de lunes a viernes.");
        }

        // Validación de solapamiento (Excluyendo la propia cita que editamos)
        boolean hayChoqueReal = appointmentRepository.checkAvailability(
                        employee, citaEditada.getStartTime(), nuevaHoraFin, citaEditada.getDate())
                .stream().anyMatch(c -> c.getId() != id);

        if (hayChoqueReal) {
            throw new SlotAlreadyOccupiedException("El barbero ya tiene otra cita en ese horario.");
        }

        // Actualizamos los datos de la Entidad
        appointmentExistente.setDate(citaEditada.getDate());
        appointmentExistente.setStartTime(citaEditada.getStartTime());
        appointmentExistente.setEndTime(nuevaHoraFin);
        appointmentExistente.setEmployee(employee);
        appointmentExistente.setService(service);

        // Guardamos y devolvemos el DTO básico (Usando tu método entityToDTO)
        appointmentRepository.save(appointmentExistente);
        return mapToDTO(appointmentExistente);
    }


    public List<AppointmentViewDTO> getAppointmentsByDate(LocalDate date) {
        List<Appointment> appointments = appointmentRepository.getAppointmentsByDate(date);

        // Convertimos la lista de Entidades a DTOs de vista
        return appointments.stream()
                .map(this::mapToViewDTO)
                .collect(Collectors.toList());
    }



    boolean checkAvailability(Appointment appointment) {
        if(appointmentRepository.checkAvailability(appointment.getEmployee(), appointment.getStartTime(), appointment.getEndTime(), appointment.getDate()).isEmpty()) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * Algoritmo principal para mostrar la agenda disponible en la App Android. permitiendo excluir una cita específica (útil para ediciones).
     * Genera tramos horarios y los filtra comparándolos con las citas reales del día.
     */
    public List<LocalTime> getAvailableHours(int empleadoId, LocalDate fecha, Integer excludeId) {
        // 1. Definimos los tramos horarios
        List<LocalTime> todosLosTramos = List.of(
                LocalTime.of(16, 0), LocalTime.of(16, 30),
                LocalTime.of(17, 0), LocalTime.of(17, 30),
                LocalTime.of(18, 0), LocalTime.of(18, 30),
                LocalTime.of(19, 0), LocalTime.of(19, 30),
                LocalTime.of(20, 0), LocalTime.of(20, 30)
        );

        // 2. Traemos todas las citas de la base de datos
        List<Appointment> citasDB = appointmentRepository.findByEmployeeIdAndDate(empleadoId, fecha);


        // Si excludeId no es nulo, filtramos; si lo es, usamos la lista completa.
        List<Appointment> citasParaValidar = (excludeId != null)
                ? citasDB.stream().filter(a -> !a.getId().equals(excludeId)).collect(Collectors.toList())
                : citasDB;

        // 4. Filtramos los tramos usando la lista que ya es segura para el lambda
        return todosLosTramos.stream()
                .filter(tramo -> isAvailable(tramo, citasParaValidar))
                .collect(Collectors.toList());
    }

    /**
     * Función auxiliar que determina si un punto específico en el tiempo (tramo)
     * cae dentro del intervalo [Inicio, Fin) de alguna cita ocupada.
     */
    private boolean isAvailable(LocalTime tramo, List<Appointment> appointments) {
        for (Appointment appointment : appointments) {
            // Si el tramo cae dentro de una cita, no está libre
            if (!tramo.isBefore(appointment.getStartTime()) && tramo.isBefore(appointment.getEndTime())) {
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
    public List<AppointmentViewDTO> buscarCitasPorNombreCliente(String nombre) {
        // Llamamos al método con la @Query personalizada que creamos en el Repository
        return appointmentRepository.findByUserName(nombre).stream().map(this::mapToViewDTO).toList();
    }

    /**
     * Convierte una entidad Cita en un objeto CitaDTO.
     * Extrae solo los IDs de las relaciones para simplificar la respuesta.
     */
    public AppointmentDTO mapToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();

        // Copiamos los datos básicos
        dto.setDate(appointment.getDate());
        dto.setStartTime(appointment.getStartTime());

        // Extraemos los IDs de los objetos relacionados
        dto.setClientId(appointment.getUser().getId());
        dto.setEmployeeId(appointment.getEmployee().getId());
        dto.setServiceId(appointment.getService().getId());

        return dto;
    }



    private AppointmentViewDTO mapToViewDTO(Appointment entity) {
        AppointmentViewDTO dto = new AppointmentViewDTO();
        dto.setId(entity.getId());
        dto.setDate(entity.getDate());
        dto.setStartTime(entity.getStartTime());

        // Extraemos los nombres de las relaciones
        dto.setCustomerName(entity.getUser().getName());
        dto.setServiceName(entity.getService().getName());
        dto.setEmployeeName(entity.getEmployee().getName());
        dto.setTelNumber(entity.getUser().getTelNumber());
        dto.setStatus(entity.getStatus());
        dto.setEmployeeId(entity.getEmployee().getId());

        // Datos extra para la card
        dto.setDurationMinutes(entity.getService().getDurationMinutes());
        dto.setPrice(entity.getService().getPrice());

        return dto;
    }

    public List<ServiceCountDTO> getTodayServiceStats() {
        return appointmentRepository.countServicesPerDay(LocalDate.now());
    }


}
