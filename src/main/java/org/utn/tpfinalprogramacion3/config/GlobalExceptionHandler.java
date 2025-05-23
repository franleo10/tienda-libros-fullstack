package org.utn.tpfinalprogramacion3.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.utn.tpfinalprogramacion3.Exceptions.DenegarPermisoEliminarReseniaException;
import org.utn.tpfinalprogramacion3.Exceptions.NoHayReseniasException;
import org.utn.tpfinalprogramacion3.Exceptions.ReseniaExistenteException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    // Maneja entidades no encontradas (404)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // Maneja argumentos inválidos en métodos (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Argumentos no válidos: " + ex.getMessage());
    }

    // Maneja errores de tipo en parámetros de ruta (400)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tipo de argumento inválido: " + ex.getMessage());
    }

    // Maneja conflictos de integridad en la base de datos (409)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Violación de integridad de datos: " + ex.getMessage());
    }

    // Maneja otros errores no específicos (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        ex.printStackTrace(); // Solo para debug, quita esto en producción
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
    }
    // Maneja argumentos ilegales (409)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    // Maneja el caso en que no existan resenias en el sistema. (404)
    public ResponseEntity<String> manejarNoHayResenias(NoHayReseniasException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Maneja el caso en el que el usuario ya hiciera una resenia a ese libro. (409)
    public ResponseEntity<String> manejarReseniaDuplicada(ReseniaExistenteException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // Maneja el caso en el que el usuario quierea eliminar una resenia no realizada por el. (403)
    public ResponseEntity<String> manejarEliminacionDeResenias(DenegarPermisoEliminarReseniaException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }
}
