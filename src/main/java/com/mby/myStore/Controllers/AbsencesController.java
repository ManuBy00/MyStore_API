package com.mby.myStore.Controllers;

import com.mby.myStore.DTO.AbsenceRequest;
import com.mby.myStore.DTO.AbsenceResponse;
import com.mby.myStore.Model.Absence;
import com.mby.myStore.Repositories.AbsenceRepository;
import com.mby.myStore.Services.AbsencesService;
import com.mby.myStore.Services.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/absences")
@CrossOrigin
@Tag(name = "Gestión de bajas o ausencias de empleados")

public class AbsencesController {
    @Autowired
    private AbsencesService absencesService;
    @Autowired
    private AppointmentService appointmentService;


    /**
     * Crea una nueva ausencia en un empleado
     *
     * @return 201 Created si tiene éxito.
     */
    @Operation(summary = "Registrar nueva ausencia", description = "Crea una ausencia vinculada a un empleado. Valida que las fechas sean coherentes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ausencia creada con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o fechas incorrectas"),
            @ApiResponse(responseCode = "404", description = "El empleado especificado no existe")
    })
    @PostMapping
    public ResponseEntity<AbsenceResponse> createAbsence(@RequestBody AbsenceRequest absence) {
        AbsenceResponse created = absencesService.createAbsence(absence);
        return new ResponseEntity<AbsenceResponse>(created, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar una ausencia", description = "Modifica las fechas o el motivo de una ausencia existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ausencia actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró la ausencia con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en la validación de fechas")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AbsenceResponse> update(@PathVariable Long id, @Valid @RequestBody AbsenceRequest absence) {
        AbsenceResponse updated = absencesService.updateAbsence(id, absence);
        return ResponseEntity.ok(updated); // Devuelve 200
    }

    @Operation(summary = "Eliminar una ausencia", description = "Borra físicamente el registro de la ausencia de la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ausencia eliminada con éxito"),
            @ApiResponse(responseCode = "404", description = "La ausencia no existe")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        absencesService.deleteAbsence(id);
        return ResponseEntity.noContent().build(); // Devuelve 204 (sin contenido, es lo estándar para delete)
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<List<AbsenceResponse>> getAbsencesByEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(absencesService.getAbsencesByEmployee(id));
    }

}
