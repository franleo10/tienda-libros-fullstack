package org.utn.tpfinalprogramacion3.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.utn.tpfinalprogramacion3.dtos.AutorDTO;
import org.utn.tpfinalprogramacion3.dtos.AutorDetalleDTO;
import org.utn.tpfinalprogramacion3.dtos.AutorResumenDTO;
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
    public ResponseEntity<String> createAutor(@Valid @RequestBody AutorDTO autorDTO) {
        return new ResponseEntity<>(autorService.createAutor(autorDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Listar autores paginados", description = "Obtiene una lista paginada de autores disponibles en el sistema.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AutorResumenDTO.class)))
    @GetMapping("/listar/{numeroPagina}")
    public ResponseEntity<Page<AutorResumenDTO>> getAutores(@PathVariable int numeroPagina) {
        return new ResponseEntity<>(autorService.listarAutores(numeroPagina), HttpStatus.OK);
    }

    @Operation(summary = "Buscar autores por nombre", description = "Busca autores por nombre, apellido o nombre completo.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AutorResumenDTO.class)))
    @GetMapping("/buscar")
    public ResponseEntity<Page<AutorResumenDTO>> buscarAutores(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "0") int pagina) {
        return ResponseEntity.ok(autorService.buscarAutores(nombre, pagina));
    }

    @GetMapping("/buscar/inactivos")
    public ResponseEntity<Page<AutorResumenDTO>> buscarAutoresInactivos(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "0") int pagina) {
        return ResponseEntity.ok(autorService.buscarAutoresInactivos(nombre, pagina));
    }

    @Operation(summary = "Obtener el detalle de un autor", description = "Obtiene un autor y sus libros según su ID.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AutorDetalleDTO.class)))
    @GetMapping("/{id}")
    public ResponseEntity<AutorDetalleDTO> obtenerDetalleAutor(@PathVariable int id) {
        return ResponseEntity.ok(autorService.obtenerDetalleAutor(id));
    }

    @Operation(summary = "Dar de baja un autor", description = "Marca un autor como inactivo sin borrar sus libros ni sus relaciones.")
    @ApiResponse(responseCode = "200", description = "Autor dado de baja")
    @PutMapping("/baja/{id}")
    public ResponseEntity<String> darDeBajaAutor(@PathVariable int id) {
        return ResponseEntity.ok(autorService.darDeBajaAutor(id));
    }

    @Operation(summary = "Reactivar un autor", description = "Vuelve a activar un autor que estaba dado de baja.")
    @ApiResponse(responseCode = "200", description = "Autor reactivado")
    @PutMapping("/alta/{id}")
    public ResponseEntity<String> darDeAltaAutor(@PathVariable int id) {
        return ResponseEntity.ok(autorService.darDeAltaAutor(id));
    }

    @Operation(summary = "Listar autores dados de baja", description = "Obtiene una lista paginada de autores inactivos.")
    @GetMapping("/listar/inactivos/{numeroPagina}")
    public ResponseEntity<Page<AutorResumenDTO>> listarAutoresInactivos(@PathVariable int numeroPagina) {
        return ResponseEntity.ok(autorService.listarAutoresInactivos(numeroPagina));
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
    public ResponseEntity<String> actualizarAutor(@PathVariable Integer id, @Valid @RequestBody AutorDTO nuevoAutor) {
        return new ResponseEntity<>(autorService.actualizarAutor(id, nuevoAutor), HttpStatus.OK);
    }

    
}
