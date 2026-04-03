package com.mby.myStore.Controllers;

import com.mby.myStore.DTO.CitaDTO;
import com.mby.myStore.Exceptions.RecordNotFoundException;
import com.mby.myStore.Exceptions.SlotAlreadyOccupiedException;
import com.mby.myStore.Model.Cita;
import com.mby.myStore.Services.CitasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/citas")
@CrossOrigin // Permite peticiones desde la App Android
@Tag(name = "Gestión de Citas", description = "Endpoints para reservar, cancelar, modificar y consultar la agenda")
public class CitasController {

    @Autowired
    private CitasService citasService;

    /**
     * Crea una nueva cita validando solapamientos.
     * @return 201 Created si tiene éxito o 409 Conflict si el barbero está ocupado.
     */
    @Operation(summary = "Crear nueva cita", description = "Registra una cita validando que el barbero no tenga otra reserva en ese horario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cita creada con éxito"),
            @ApiResponse(responseCode = "409", description = "Conflicto: El horario ya está ocupado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "Usuario no autenticado")
    })
    @PostMapping
    public ResponseEntity<?> createCita(@RequestBody CitaDTO cita) {
        try {
            citasService.createCita(cita);
            return ResponseEntity.status(HttpStatus.CREATED).body(cita);
        } catch (SlotAlreadyOccupiedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }




    /**
     * Actualiza una cita existente.
     */
    @Operation(summary = "Crear nueva cita", description = "Registra una cita validando solapamientos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cita creada correctamente"),
            @ApiResponse(responseCode = "409", description = "Conflicto: El barbero ya tiene una cita en ese horario"),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada"),
            @ApiResponse(responseCode = "403", description = "Usuario no autenticado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCita(@PathVariable int id, @RequestBody CitaDTO cita) {
        try {
            Cita actualizada = citasService.updateCita(id, cita);
            return ResponseEntity.ok(actualizada); // Devolvemos el objeto, no un String
        } catch (RecordNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SlotAlreadyOccupiedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }


    @Operation(summary = "Eliminar cita", description = "Elimina una cita existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cita eliminada con éxito"),
            @ApiResponse(responseCode = "404", description = "No encontrada: La cita requerida no existe"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "Usuario no autenticado")
    })
    /**
     * Elimina una cita.
     */
    @DeleteMapping("/{cita}")
    public ResponseEntity<?> deleteCita(@PathVariable int cita) {
        try {
            citasService.deleteCita(cita);
            return ResponseEntity.noContent().build();
        } catch (RecordNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Lista citas por fecha.
     * Uso: /citas/fecha?fecha=2026-05-10
     */
    @Operation(summary = "Listar citas por fecha",
            description = "Recupera todas las citas programadas en la barbería para un día concreto. Formato: yyyy-mm-dd")
    @GetMapping("/fecha")
    public ResponseEntity<List<CitaDTO>> listarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(citasService.getCitasByFecha(fecha));
    }

    /**
     * Endpoint clave para la App: Devuelve las horas disponibles en tramos de 30min.
     * Uso: /citas/disponibilidad?empleadoId=1&fecha=2026-05-10
     */
    @Operation(summary = "Obtener horas libres",
            description = "Calcula los huecos disponibles de un barbero específico en una fecha determinada, devolviendo tramos de 30 minutos.")
    @GetMapping("/disponibilidad")
    public ResponseEntity<List<LocalTime>> obtenerDisponibilidad(
            @RequestParam int empleadoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<LocalTime> horasLibres = citasService.obtenerHorasLibres(empleadoId, fecha);
        return ResponseEntity.ok(horasLibres);
    }

    /**
     * Busca citas filtrando por el nombre del cliente (parcial o completo).
     * Demuestra el uso de JPQL con JOIN manual entre Cita y Cliente.
     */
    @Operation(summary = "Buscar citas por nombre de cliente",
            description = "Realiza una búsqueda relacional (JOIN) para encontrar citas filtrando por el nombre del cliente.")
    @GetMapping("/buscar/cliente")
    public ResponseEntity<List<CitaDTO>> buscarPorNombre(@RequestParam String nombre) {
        List<CitaDTO> citas = citasService.buscarCitasPorNombreCliente(nombre);
        return ResponseEntity.ok(citas);
    }
}