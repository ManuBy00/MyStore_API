package com.mby.myStore.Controllers;


import com.mby.myStore.DTO.ClienteDTO;
import com.mby.myStore.DTO.LoginData;
import com.mby.myStore.Exceptions.DuplicateRecordException;
import com.mby.myStore.Exceptions.InvalidCredentialsException;
import com.mby.myStore.Exceptions.RecordNotFoundException;
import com.mby.myStore.Model.Cliente;
import com.mby.myStore.Services.ClientesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@Tag(name = "Gestión de Clientes", description = "Operaciones CRUD y búsqueda para la administración de clientes")
public class ClientesController {

    @Autowired
    ClientesService clienteServices;

    @CrossOrigin
    @GetMapping
    @Operation(summary = "Obtener todos los clientes", description = "Retorna una lista con todos los clientes registrados en la base de datos.")
    @ApiResponse(responseCode = "200", description = "Lista de clientes recuperada con éxito")
    public ResponseEntity<List<ClienteDTO>> getClientes(){
        List<ClienteDTO> clientes = clienteServices.getAll();
        return ResponseEntity.ok(clientes);
    }

    @CrossOrigin
    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID", description = "Busca un cliente específico en el sistema mediante su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "No se encontró el cliente con el ID proporcionado")
    })
    public ResponseEntity<?> getClienteById(
            @Parameter(description = "ID del cliente a buscar", example = "1")
            @PathVariable int id){
        try {
            ClienteDTO cliente = clienteServices.getClienteById(id);
            return ResponseEntity.ok(cliente);
        } catch (RecordNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @CrossOrigin
    @PostMapping
    @Operation(summary = "Registrar nuevo cliente", description = "Crea un nuevo perfil de cliente. El email debe ser único en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente registrado correctamente"),
            @ApiResponse(responseCode = "409", description = "Conflicto: Ya existe un cliente con ese email"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<?> createCliente(@RequestBody Cliente cliente){
        try {
            clienteServices.addCliente(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
        } catch (DuplicateRecordException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un cliente", description = "Borra permanentemente el registro de un cliente del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente eliminado con éxito (No Content)"),
            @ApiResponse(responseCode = "404", description = "El cliente no existe"),
            @ApiResponse(responseCode = "409", description = "Conflicto: El cliente tiene citas asociadas y no puede ser borrado")
    })
    public ResponseEntity<?> deleteCliente(
            @Parameter(description = "ID del cliente a eliminar", example = "5")
            @PathVariable int id){
        try {
            clienteServices.deleteCliente(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RecordNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @CrossOrigin
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente", description = "Modifica los datos de un cliente existente identificado por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "El cliente solicitado no existe"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados")
    })
    public ResponseEntity<?> updateCliente(
            @Parameter(description = "ID del cliente a actualizar", example = "1")
            @PathVariable int id,
            @RequestBody Cliente cliente){
        try{
            ClienteDTO clienteActualizado = clienteServices.updateCliente(cliente, id);
            return ResponseEntity.ok(clienteActualizado);
        } catch (RecordNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar clientes por nombre", description = "Filtra la lista de clientes buscando coincidencias parciales por nombre.")
    @ApiResponse(responseCode = "200", description = "Lista de coincidencias encontrada")
    public ResponseEntity<List<ClienteDTO>> searchClientes(
            @Parameter(description = "Nombre o fragmento del nombre a buscar", example = "Juan")
            @RequestParam String nombre) {
        return ResponseEntity.ok(clienteServices.getClientesByNombre(nombre));
    }
}