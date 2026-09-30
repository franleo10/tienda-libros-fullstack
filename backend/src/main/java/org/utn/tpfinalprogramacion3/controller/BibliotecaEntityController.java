package org.utn.tpfinalprogramacion3.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.dtos.AgregarLibroDTO;
import org.utn.tpfinalprogramacion3.dtos.BibliotecaDTO;
import org.utn.tpfinalprogramacion3.dtos.LibroBibliotecaDTO;
import org.utn.tpfinalprogramacion3.services.BibliotecaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/biblioteca")
@RequiredArgsConstructor
public class BibliotecaEntityController {

    private final BibliotecaService bibliotecaService;

    @Operation(summary = "Agregar libro a la biblioteca de un usuario", description = "Agrega un libro a la biblioteca personal de un usuario específico.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @PostMapping("/agregar-libro/{idUsuario}")
    public ResponseEntity<String> agregarLibro(
            @PathVariable int idUsuario,
            @RequestBody AgregarLibroDTO dto) {

        dto.setIdUsuario(idUsuario);
        bibliotecaService.agregarLibro(dto);
        return ResponseEntity.ok("Libro agregado a la biblioteca del usuario");
    }

    @Operation(summary = "Listar todas las bibliotecas", description = "Obtiene la lista completa de bibliotecas de todos los usuarios.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BibliotecaDTO.class))))
    @GetMapping("/listar")
    public ResponseEntity<List<BibliotecaDTO>> getAll() {
        return ResponseEntity.ok(bibliotecaService.findAll());
    }

    @Operation(summary = "Obtener biblioteca del usuario actual", description = "Obtiene la biblioteca asociada al usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BibliotecaDTO.class)))
    @GetMapping("/usuario")
    public ResponseEntity<BibliotecaDTO> getByUsuario() {
        BibliotecaDTO dto = bibliotecaService.getByUsuarioId();
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Obtener biblioteca por ID de usuario", description = "Obtiene la biblioteca asociada a un usuario específico, utilizando su ID. Este endpoint es ideal para administradores.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BibliotecaDTO.class)))
    @GetMapping("/listar/{idUsuario}")
    public ResponseEntity<BibliotecaDTO> getByUsuario(@PathVariable int idUsuario) {
        BibliotecaDTO dto = bibliotecaService.getByUsuarioIdForAdmin(idUsuario);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Eliminar biblioteca", description = "Elimina una biblioteca específica según su ID.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> deleteBiblioteca(@PathVariable int id) {
        bibliotecaService.deleteById(id);
        return ResponseEntity.ok("Biblioteca eliminada correctamente.");
    }

    @Operation(summary = "Obtener libros favoritos de un usuario", description = "Recupera la lista de libros marcados como favoritos por un usuario específico.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LibroBibliotecaDTO.class))))
    @GetMapping("/favoritos/{idUsuario}")
    public ResponseEntity<List<LibroBibliotecaDTO>> getLibrosFavoritos(@PathVariable int idUsuario) {
        List<LibroBibliotecaDTO> favoritos = bibliotecaService.getLibrosFavoritos(idUsuario);
        return ResponseEntity.ok(favoritos);
    }

    @Operation(summary = "Marcar libro como favorito", description = "Marca un libro específico como favorito para un usuario determinado.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @PostMapping("/favoritos/{idUsuario}/{idLibro}")
    public ResponseEntity<String> marcarLibroFavorito(@PathVariable int idUsuario, @PathVariable int idLibro) {
        bibliotecaService.marcarLibroFavorito(idUsuario, idLibro);
        return ResponseEntity.ok("Libro marcado como favorito!!");
    }

    @Operation(summary = "Desmarcar libro como favorito", description = "Elimina la marca de favorito de un libro específico para un usuario determinado.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @DeleteMapping("/favoritos/{idUsuario}/{idLibro}")
    public ResponseEntity<String> desmarcarLibroFavorito(@PathVariable int idUsuario, @PathVariable int idLibro) {
        bibliotecaService.desmarcarLibroFavorito(idUsuario, idLibro);
        return ResponseEntity.ok("Libro desmarcado como favorito!");
    }

}
