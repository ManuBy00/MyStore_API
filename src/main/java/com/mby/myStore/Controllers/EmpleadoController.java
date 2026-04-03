package com.mby.myStore.Controllers;

import com.mby.myStore.Exceptions.RecordNotFoundException;
import com.mby.myStore.Model.Empleado;
import com.mby.myStore.Services.EmpleadosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empleados")
@Tag(name = "Gestión de empleados", description = "Endpoints para añadir, cancelar, modificar y buscar empleados")
@CrossOrigin
public class EmpleadoController {

    @Autowired
    private EmpleadosService empleadoService;

    /**
     * Obtiene el listado de todos los barberos disponibles.
     * @return 200 OK con la lista de empleados.
     */
    @Operation(summary = "Mostrar lista de empleados", description = "Devuelve todos los empleados registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista devuelta con éxito"),
    })
    @GetMapping
    public ResponseEntity<List<Empleado>> getAll() {
        return ResponseEntity.ok(empleadoService.getAll());
    }

    /**
     * Busca un empleado por su ID.
     * @param id Identificador único.
     * @return 200 OK si existe, 404 Not Found si el ID es erróneo.
     */
    @Operation(summary = "Buscar empleado por id", description = "devuelve el empleado que coincida con el id introducido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado encontrado"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado"),
            @ApiResponse(responseCode = "403", description = "Usuario no autenticado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(empleadoService.getEmpleadoById(id));
        } catch (RecordNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Registra un nuevo empleado en la base de datos de la barbería.
     * @param empleado Datos del empleado (nombre, etc).
     * @return 201 Created con el objeto guardado.
     */
    @Operation(summary = "añadir empleado", description = "devuelve el empleado que coincida con el id introducido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empleado creado"),
            @ApiResponse(responseCode = "403", description = "Usuario no autenticado")
    })
    @PostMapping
    public ResponseEntity<Empleado> add(@RequestBody Empleado empleado) {
        empleadoService.addEmpleado(empleado);
        return ResponseEntity.status(HttpStatus.CREATED).body(empleado);
    }

    /**
     * Actualiza los datos de un empleado existente.
     * @param id ID del empleado a modificar.
     * @param empleado Datos nuevos.
     * @return 200 OK con mensaje de éxito o 404 si no existe.
     */
    @Operation(summary = "Actualizar empleado", description = "Actualiza los datos de un empleado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Datos actualizados"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado"),
            @ApiResponse(responseCode = "403", description = "Usuario no autenticado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Empleado empleado) {
        try {
           Empleado emp = empleadoService.updateEmpleado(id, empleado);
            return ResponseEntity.ok(empleado);
        } catch (RecordNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Elimina un empleado del sistema.
     * @param id ID del empleado a borrar.
     * @return 204 No Content si se borra con éxito o 404 si no se encuentra.
     */
    @Operation(summary = "Eliminar empleado", description = "Elimina un empleado de la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Empleado eliminado"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado"),
            @ApiResponse(responseCode = "403", description = "Usuario no autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            empleadoService.deleteEmpleado(id);
            return ResponseEntity.noContent().build();
        } catch (RecordNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}