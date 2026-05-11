package com.mby.myStore.Controllers;

import com.mby.myStore.Model.Employee;
import com.mby.myStore.Services.EmployeeService;
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
@RequestMapping("/employees")
@Tag(name = "Gestión de empleados", description = "Endpoints para añadir, cancelar, modificar y buscar empleados")
@CrossOrigin
public class EmployeeController {

    @Autowired
    private EmployeeService empleadoService;

    /**
     * Obtiene el listado de todos los barberos disponibles.
     * @return 200 OK con la lista de empleados.
     */
    @Operation(summary = "Mostrar lista de empleados", description = "Devuelve todos los empleados registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista devuelta con éxito"),
    })
    @GetMapping
    public ResponseEntity<List<Employee>> getAll() {
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
    public ResponseEntity<Employee> getById(@PathVariable Long id) {
        return ResponseEntity.ok(empleadoService.getEmployeeById(id));
    }

    /**
     * Registra un nuevo empleado en la base de datos de la barbería.
     * @param employee Datos del empleado (nombre, etc).
     * @return 201 Created con el objeto guardado.
     */
    @Operation(summary = "añadir empleado", description = "devuelve el empleado que coincida con el id introducido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empleado creado"),
            @ApiResponse(responseCode = "403", description = "Usuario no autenticado")
    })
    @PostMapping
    public ResponseEntity<Employee> add(@RequestBody Employee employee) {
        empleadoService.addEmployee(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    /**
     * Actualiza los datos de un empleado existente.
     * @param id ID del empleado a modificar.
     * @param employee Datos nuevos.
     * @return 200 OK con mensaje de éxito o 404 si no existe.
     */
    @Operation(summary = "Actualizar empleado", description = "Actualiza los datos de un empleado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Datos actualizados"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado"),
            @ApiResponse(responseCode = "403", description = "Usuario no autenticado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(@PathVariable Long id, @RequestBody Employee employee) {
        Employee emp = empleadoService.updateEmployee(id, employee);
        return ResponseEntity.ok(emp);
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
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        empleadoService.deleteEmployee(id);
        return ResponseEntity.noContent().build();

    }
}