package org.utn.tpfinalprogramacion3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.dtos.GeneroDTO;
import org.utn.tpfinalprogramacion3.entities.GeneroEntity;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.repository.GeneroRepository;
import org.utn.tpfinalprogramacion3.services.GeneroService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/generos")
public class GeneroEntityController {
    private GeneroRepository generoRepository;
    private GeneroService generoService;

    public GeneroEntityController(GeneroRepository generoRepository, GeneroService generoService) {
        this.generoRepository = generoRepository;
        this.generoService = generoService;
    }

    @Operation(summary = "Crear un nuevo género", description = "Crea un nuevo género y devuelve el objeto creado.")
    @ApiResponse(responseCode = "201", description = "CREATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeneroDTO.class)))
    @PostMapping("/crear")
    public GeneroDTO crearGenero(@RequestBody GeneroDTO generoDTO) {
        return generoService.Save(generoDTO);
    }

    @Operation(summary = "Listar todos los géneros", description = "Obtiene la lista completa de géneros disponibles.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GeneroEntity.class))))
    @GetMapping("/listar")
    public ResponseEntity<List<GeneroEntity>> listarGeneros() {
        return ResponseEntity.ok(generoService.findAll());
    }

    @Operation(summary = "Obtener libros por género", description = "Obtiene la lista de libros que pertenecen al género especificado por nombre.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LibroEntity.class))))
    @GetMapping("/libros")
    public ResponseEntity<List<LibroEntity>> obtenerLibrosPorGenero(@RequestParam String nombre) {
        List<LibroEntity> libros = generoService.obtenerLibrosPorNombreGenero(nombre);
        return ResponseEntity.ok(libros);
    }

}
