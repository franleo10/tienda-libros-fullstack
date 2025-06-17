package org.utn.tpfinalprogramacion3.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.http.protocol.ResponseServer;
import org.hibernate.validator.cfg.defs.pl.REGONDef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.dtos.LibroDTO;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.services.LibroService;

import java.util.List;

@RestController
@RequestMapping("/libro")
public class LibroEntityController {

    private final LibroService libroService;

    public LibroEntityController(LibroService libroService) {
        this.libroService = libroService;
    }

    @Operation(summary = "Crear un nuevo libro", description = "Crea un libro y lo asocia a un autor y un género por sus IDs.")
    @ApiResponse(responseCode = "201", description = "CREATED", content = @Content(schema = @Schema(implementation = LibroDTO.class)))
    @PostMapping("/crear/{idAutor}/{idGenero}")
    public ResponseEntity<LibroDTO> crearLibro(@RequestBody LibroDTO libro, @PathVariable int idAutor,
                                               @PathVariable int idGenero) {
        if (this.libroService.crearLibro(libro, idAutor, idGenero).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(libro, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar libros activos paginados", description = "Devuelve una lista paginada de libros activos.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = LibroDTO.class))))
    @GetMapping("/listar/{numeroPagina}")
    public ResponseEntity<Page<LibroDTO>> obtenerLibros(@PathVariable int numeroPagina) {
        return new ResponseEntity<>(libroService.getAllLibros(numeroPagina), HttpStatus.OK);
    }

    @Operation(summary = "Listar libros inactivos paginados", description = "Devuelve una lista paginada de libros dados de baja.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = LibroDTO.class))))
    @GetMapping("/listar/inactivos/{numeroPagina}")
    public ResponseEntity<Page<LibroDTO>> obtenerLibrosInactivos(@PathVariable int numeroPagina) {
        return new ResponseEntity<>(libroService.getAllLibrosInactivos(numeroPagina), HttpStatus.OK);
    }

    @Operation(summary = "Agregar un género a un libro", description = "Asocia un género existente a un libro.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = String.class)))
    @PutMapping("/{idLibro}/agregar_genero/{idGenero}")
    public ResponseEntity<String> agregarGeneroALibro(@PathVariable int idLibro, @PathVariable int idGenero) {
        return new ResponseEntity<>(libroService.agregarGeneroALibro(idLibro, idGenero), HttpStatus.OK);
    }

    @Operation(summary = "Agregar un autor a un libro", description = "Asocia un autor existente a un libro.")
    @ApiResponse(responseCode = "201", description = "CREATED", content = @Content(schema = @Schema(implementation = String.class)))
    @PutMapping("/{idLibro}/agregar_autor/{idAutor}")
    public ResponseEntity<String> agregarAutorALibro(@PathVariable int idLibro, @PathVariable int idAutor) {
        return new ResponseEntity<>(libroService.agregarAutorALibro(idLibro, idAutor), HttpStatus.CREATED);
    }

    @Operation(summary = "Eliminar un autor de un libro", description = "Quita la asociación entre un libro y un autor.")
    @ApiResponse(responseCode = "204", description = "NO CONTENT", content = @Content(schema = @Schema(implementation = String.class)))
    @DeleteMapping("/{idLibro}/eliminar_autor/{idAutor}")
    public ResponseEntity<String> eliminarAutorALibro(@PathVariable int idLibro, @PathVariable int idAutor) {
        return new ResponseEntity<>(libroService.eliminarAutorALibro(idLibro, idAutor), HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Crear un libro usando OpenLibrary", description = "Crea un libro con datos obtenidos automáticamente de la API de OpenLibrary.")
    @ApiResponse(responseCode = "201", description = "CREATED", content = @Content(schema = @Schema(implementation = LibroDTO.class)))
    @PostMapping("/openlibrary/{generoId}")
    public ResponseEntity<LibroDTO> crearLibroConGenero(
            @RequestParam String titulo,
            @PathVariable Integer generoId) {
        LibroDTO libroDTO = new LibroDTO();
        libroDTO.setTitulo(titulo);

        LibroDTO creado = libroService.crearLibro2(libroDTO, generoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Dar de baja un libro", description = "Realiza una baja lógica del libro.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = String.class)))
    @PutMapping("/baja/{idLibro}")
    public ResponseEntity<String> bajaLogicaLibro(@PathVariable int idLibro) {
        return new ResponseEntity<>(libroService.bajaLogicaLibro(idLibro), HttpStatus.OK);
    }

    @Operation(summary = "Dar de alta un libro", description = "Activa un libro que había sido dado de baja.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = String.class)))
    @PutMapping("/alta/{idLibro}")
    public ResponseEntity<String> altaLogicaLibro(@PathVariable int idLibro) {
        return new ResponseEntity<>(libroService.altaLogicaLibro(idLibro), HttpStatus.OK);
    }

    @Operation(summary = "Buscar libro por nombre", description = "Busca un libro por su título.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LibroDTO.class)))
    @GetMapping("/buscar/nombre/{titulo}")
    public ResponseEntity<LibroDTO> buscarPorNombre(@PathVariable String titulo) {
        return libroService.buscarLibroPorNombre(titulo);
    }

    @Operation(summary = "Buscar libro por ID", description = "Busca un libro por su ID.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LibroDTO.class)))
    @GetMapping("/buscar/id/{idLibro}")
    public ResponseEntity<LibroDTO> buscarPorId(@PathVariable int idLibro) {
        return libroService.buscarLibroPorId(idLibro);
    }

}
