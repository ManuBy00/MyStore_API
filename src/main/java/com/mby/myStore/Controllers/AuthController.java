package com.mby.myStore.Controllers;

import com.mby.myStore.DTO.LoginData;
import com.mby.myStore.DTO.LoginResponse;
import com.mby.myStore.Exceptions.DuplicateRecordException;
import com.mby.myStore.Exceptions.InvalidCredentialsException;
import com.mby.myStore.Exceptions.RecordNotFoundException;
import com.mby.myStore.Model.Cliente;
import com.mby.myStore.Security.JwtService;
import com.mby.myStore.Services.ClientesService;
import com.mby.myStore.Utils.HashPsw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private ClientesService clientesService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginData loginData) {
        try {
            // Comprobar email y password en la DB
            Cliente cliente = clientesService.login(loginData.getEmail(), loginData.getPassword());

            // Si ha llegado aquí es que el login es correcto. Generamos la "llave" (token)
            // Usamos el email como identificador único en el token
            String token = jwtService.generateToken(cliente.getEmail());

            // Devolvemos TODO: el token para que Android lo guarde y el cliente para la UI
            return ResponseEntity.ok(new LoginResponse(token, cliente));

        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect credentials");
        } catch (RecordNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    /**
     * Registro de un nuevo cliente.
     * Al ser una ruta /auth, no requerirá token en SecurityConfig.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registrarCliente(@RequestBody Cliente cliente) {
        try {
            cliente.setPassword(HashPsw.hashPassword(cliente.getPassword())); //hash de password
            // Guardamos el cliente usando el servicio que ya tienes
            clientesService.addCliente(cliente);

            //generar el token directamente para que
            //tras registrarse entre automáticamente a la App
            String token = jwtService.generateToken(cliente.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(new LoginResponse(token, cliente));

        } catch (DuplicateRecordException e) {
            // Si el email ya existe, devolvemos un 409 Conflict
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El email ya está registrado");
        }
    }
}
