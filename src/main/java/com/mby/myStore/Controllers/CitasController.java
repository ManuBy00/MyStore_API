package com.mby.myStore.Controllers;

import com.mby.myStore.Exceptions.RecordNotFoundException;
import com.mby.myStore.Exceptions.SlotAlreadyOccupiedException;
import com.mby.myStore.Model.Cita;
import com.mby.myStore.Services.CitasService;
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
public class CitasController {


    @Autowired
    private CitasService citasService;

    /**
     * Crea una nueva cita validando solapamientos.
     * @return 201 Created si tiene éxito o 409 Conflict si el barbero está ocupado.
     */
    @PostMapping
    public ResponseEntity<?> crearCita(@RequestBody Cita cita) {
        try {
            citasService.addCita(cita);
            return ResponseEntity.status(HttpStatus.CREATED).body(cita);
        } catch (SlotAlreadyOccupiedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * Actualiza una cita existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Cita cita) {
        try {
            citasService.updateCita(id, cita);
            return ResponseEntity.ok("Cita actualizada correctamente");
        } catch (RecordNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SlotAlreadyOccupiedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * Elimina una cita.
     */
    @DeleteMapping
    public ResponseEntity<?> eliminar(@RequestBody Cita cita) {
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
    @GetMapping("/fecha")
    public ResponseEntity<List<Cita>> listarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(citasService.getCitasByFecha(fecha));
    }

    /**
     * Endpoint clave para la App: Devuelve las horas disponibles en tramos de 30min.
     * Uso: /citas/disponibilidad?empleadoId=1&fecha=2026-05-10
     */
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
    @GetMapping("/buscar/cliente")
    public ResponseEntity<List<Cita>> buscarPorNombre(@RequestParam String nombre) {
        List<Cita> citas = citasService.buscarCitasPorNombreCliente(nombre);
        return ResponseEntity.ok(citas);
    }
}