package com.mby.myStore.Controllers;

import com.mby.myStore.Exceptions.RecordNotFoundException;
import com.mby.myStore.Model.Empleado;
import com.mby.myStore.Services.EmpleadosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empleados")
@CrossOrigin
public class EmpleadoController {

    @Autowired
    private EmpleadosService empleadoService;

    /**
     * Obtiene el listado de todos los barberos disponibles.
     * @return 200 OK con la lista de empleados.
     */
    @GetMapping
    public ResponseEntity<List<Empleado>> getAll() {
        return ResponseEntity.ok(empleadoService.getAll());
    }

    /**
     * Busca un empleado por su ID.
     * @param id Identificador único.
     * @return 200 OK si existe, 404 Not Found si el ID es erróneo.
     */
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
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Empleado empleado) {
        try {
            empleadoService.updateEmpleado(id, empleado);
            return ResponseEntity.ok("Empleado actualizado correctamente");
        } catch (RecordNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Elimina un empleado del sistema.
     * @param id ID del empleado a borrar.
     * @return 204 No Content si se borra con éxito o 404 si no se encuentra.
     */
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