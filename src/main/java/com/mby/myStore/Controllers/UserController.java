package com.mby.myStore.Controllers;

import com.mby.myStore.DTO.UserDTO;
import com.mby.myStore.Exceptions.DuplicateRecordException;
import com.mby.myStore.Exceptions.RecordNotFoundException;
import com.mby.myStore.Model.User;
import com.mby.myStore.Services.UserService;
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
@RequestMapping("/users")
@Tag(name = "Gestión de usuarios", description = "Operaciones CRUD y búsqueda para la administración de usuarios")
public class UserController {

    @Autowired
    UserService userService;

    @CrossOrigin
    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista con todos los usuarios registrados en la base de datos.")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios recuperada con éxito")
    public ResponseEntity<List<UserDTO>> getUsers(){
        List<UserDTO> usuarios = userService.getAll();
        return ResponseEntity.ok(usuarios);
    }

    @CrossOrigin
    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Busca un usuario específico en el sistema mediante su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario con el ID proporcionado")})
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "ID del usuario a buscar", example = "1")
            @PathVariable int id){
            UserDTO usuario = userService.getUserById(id);
            return ResponseEntity.ok(usuario);

    }

    @CrossOrigin
    @PostMapping
    @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo perfil de usuario. El email debe ser único en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuairo registrado correctamente"),
            @ApiResponse(responseCode = "409", description = "Conflicto: Ya existe un usuario con ese email"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<UserDTO> createUser(@RequestBody User user){
        userService.addUser(user);
        UserDTO userdto = userService.entityToDTO(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userdto);
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario", description = "Borra permanentemente el registro de un usuario del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "usuario eliminado con éxito (No Content)"),
            @ApiResponse(responseCode = "404", description = "El usuario no existe"),
            @ApiResponse(responseCode = "409", description = "Conflicto: El usuario tiene citas asociadas y no puede ser borrado")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID del usuario a eliminar", example = "5")
            @PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @CrossOrigin
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Modifica los datos de un usuario existente identificado por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "El usuario solicitado no existe"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados")
    })
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "ID del usuario a actualizar", example = "1")
            @PathVariable int id,
            @RequestBody User user){
        UserDTO clienteActualizado = userService.updateUser(user, id);
        return ResponseEntity.ok(clienteActualizado);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar usuarios por nombre", description = "Filtra la lista de usuarios buscando coincidencias parciales por nombre.")
    @ApiResponse(responseCode = "200", description = "Lista de coincidencias encontrada")
    public ResponseEntity<List<UserDTO>> searchUser(
            @Parameter(description = "Nombre o fragmento del nombre a buscar", example = "Juan")
            @RequestParam String name) {
        return ResponseEntity.ok(userService.getUsersByName(name));
    }
}