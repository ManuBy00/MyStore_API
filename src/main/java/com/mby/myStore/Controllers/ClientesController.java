package com.mby.myStore.Controllers;


import com.mby.myStore.DTO.LoginData;
import com.mby.myStore.Exceptions.DuplicateRecordException;
import com.mby.myStore.Exceptions.InvalidCredentialsException;
import com.mby.myStore.Exceptions.RecordNotFoundException;
import com.mby.myStore.Model.Cliente;
import com.mby.myStore.Services.ClientesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClientesController {

    @Autowired
    ClientesService clienteServices;

    /**
     * Obtiene la lista completa de clientes registrados en el sistema.
     * @return ResponseEntity con la lista de clientes y estado 200 OK.
     */
    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<Cliente>> getClientes(){
        List<Cliente> clientes = clienteServices.getAll();
        return ResponseEntity.ok(clientes);
    }


    /**
     * Busca un cliente específico mediante su identificador único.
     * @param id Identificador primario del cliente.
     * @return ResponseEntity con el cliente encontrado o mensaje de error 404 si no existe.
     */
    @CrossOrigin
    @GetMapping("/{id}")
    public ResponseEntity<?> getClienteById(@PathVariable int id){
        try {
            Cliente cliente = clienteServices.getClienteById(id);
            return ResponseEntity.ok(cliente);
        }catch (RecordNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Registra un nuevo cliente en la base de datos.
     * @param cliente Objeto cliente con los datos a persistir.
     * @return ResponseEntity con el cliente creado (201) o error de duplicidad (409).
     */
    @CrossOrigin
    @PostMapping
    public ResponseEntity<?> createCliente(@RequestBody Cliente cliente){
        try {
            clienteServices.addCliente(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
        }catch (DuplicateRecordException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * Elimina un cliente del sistema permanentemente.
     * @param id Identificador del cliente a eliminar.
     * @return ResponseEntity con estado 204 (No Content) o 404 si el ID no existe.
     */
    @CrossOrigin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCliente(@PathVariable int id){
        try {
            clienteServices.deleteCliente(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }catch (RecordNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Actualiza la información de un cliente existente.
     * @param id Identificador del cliente a modificar.
     * @param cliente Objeto con los nuevos datos del cliente.
     * @return ResponseEntity con mensaje de éxito o 404 si no se encuentra el recurso.
     */
    @CrossOrigin
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCliente(@PathVariable int id, @RequestBody Cliente cliente){
        try{
            clienteServices.updateCliente(cliente, id);
            return ResponseEntity.ok("Cliente Actualizado Correctamente");
        }catch (RecordNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Filtra clientes cuyo nombre contenga la cadena de texto proporcionada.
     * @param nombre Texto o patrón de búsqueda para el nombre.
     * @return ResponseEntity con la lista de coincidencias (case-insensitive).
     */
    @GetMapping("/search")
    public ResponseEntity<List<Cliente>> searchClientes(@RequestParam String nombre) {
        return ResponseEntity.ok(clienteServices.getClientesByNombre(nombre));
    }
}
