package org.utn.tpfinalprogramacion3.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.dtos.AutorDTO;
import org.utn.tpfinalprogramacion3.dtos.LibroDTO;
import org.utn.tpfinalprogramacion3.entities.AutorEntity;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.repository.AutorRepository;
import org.utn.tpfinalprogramacion3.services.AutorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/autores")
public class AutorEntityController {

    private final AutorRepository autorRepository;
    private final AutorService autorService;

    @Autowired
    public AutorEntityController(AutorService autorService, AutorRepository autorRepository) {
        this.autorService = autorService;
        this.autorRepository = autorRepository;
    }

    @Operation(summary = "Crear un autor", description = "Crea un nuevo autor en el sistema.")
    @ApiResponse(responseCode = "201", description = "CREATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @PostMapping("/crear")
    public ResponseEntity<String> createAutor(@RequestBody AutorDTO autorDTO) {
        return new ResponseEntity<>(autorService.createAutor(autorDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Listar autores paginados", description = "Obtiene una lista paginada de autores disponibles en el sistema.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AutorDTO.class)))
    @GetMapping("/listar/{numeroPagina}")
    public ResponseEntity<Page<AutorDTO>> getAutores(@PathVariable int numeroPagina) {
        return new ResponseEntity<>(autorService.listarAutores(numeroPagina), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar un autor", description = "Elimina un autor específico según su ID.")
    @ApiResponse(responseCode = "204", description = "NO CONTENT", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> deleteAutor(@PathVariable int id) {
        return new ResponseEntity<>(autorService.borrarAutor(id), HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Listar autores por nombre", description = "Obtiene una lista paginada de autores cuyo nombre coincida (total o parcialmente) con el valor proporcionado.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AutorDTO.class)))
    @GetMapping("/listar/nombre/{nombre}/{numeroPagina}")
    public ResponseEntity<Page<AutorDTO>> getAutoresByName(@PathVariable String nombre,
            @PathVariable int numeroPagina) {
        return new ResponseEntity<>(autorService.listarPorNombre(nombre, numeroPagina), HttpStatus.OK);
    }

    @Operation(summary = "Listar libros por nombre de autor", description = "Obtiene una lista paginada de libros escritos por un autor cuyo nombre coincida (total o parcialmente) con el valor proporcionado.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LibroDTO.class)))
    @GetMapping("/nombre/{numeroPagina}")
    public ResponseEntity<Page<LibroDTO>> obtenerLibrosPorAutor(@RequestParam String nombre,
            @PathVariable int numeroPagina) {
        return new ResponseEntity<>(autorService.obtenerLibrosPorNombreAutor(nombre, numeroPagina), HttpStatus.OK);
    }

    @Operation(summary = "Actualizar un autor", description = "Actualiza la información de un autor existente según su ID.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @PutMapping("/actualizar-autor/{id}")
    public ResponseEntity<String> actualizarAutor(@PathVariable Integer id, @RequestBody AutorEntity nuevoAutor) {
        return new ResponseEntity<>(autorService.actualizarAutor(id, nuevoAutor), HttpStatus.OK);
    }

    
}
