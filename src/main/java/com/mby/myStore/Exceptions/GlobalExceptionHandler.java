package com.mby.myStore.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 404 Not Found - Para cuando algo no existe
    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<String> handleNotFound(RecordNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // 2. 409 Conflict - Para solapamientos de horario
    @ExceptionHandler(SlotAlreadyOccupiedException.class)
    public ResponseEntity<String> handleSlotOccupied(SlotAlreadyOccupiedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    // 3. 409 Conflict - Para registros duplicados (ej: mismo email o DNI)
    @ExceptionHandler(DuplicateRecordException.class)
    public ResponseEntity<String> handleDuplicate(DuplicateRecordException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    // 4. 403 Unauthorized - Para fallos de login/credenciales
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    // 5. 500 Internal Server Error -  para errores inesperados
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ha ocurrido un error inesperado: " + ex.getMessage());
    }

    // 6. 400 Bad Request - Para fechas pasadas o fines de semana
    @ExceptionHandler(DateNotValidException.class)
    public ResponseEntity<String> handleDateNotValid(DateNotValidException ex) {
        // Devolvemos el mensaje puesto al lanzar la excepción
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    //7. 401 token expirado
    @ExceptionHandler(ExpiredJwtToken.class)
    public ResponseEntity<String> handleExpiredJwtToken(ExpiredJwtToken token) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(token.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleDateError(MethodArgumentNotValidException ex) {
        // Pillamos el primer error de validación que encuentre
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }


}