package org.utn.tpfinalprogramacion3.controller;

import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.dtos.ReseniaCreateDTO;
import org.utn.tpfinalprogramacion3.dtos.ReseniaDTO;
import org.utn.tpfinalprogramacion3.repository.ReseniaRepository;
import org.utn.tpfinalprogramacion3.services.ReseniaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@RestController
@RequestMapping("/resenias")
public class ReseniaEntityController {

    private final ReseniaService reseniaService;

    @Autowired
    public ReseniaEntityController(ReseniaService reseniaService) {
        this.reseniaService = reseniaService;
    }

    @Operation(summary = "Crear una reseña", description = "Crea una nueva reseña asociada a un usuario y un libro específicos.")
    @ApiResponse(responseCode = "201", description = "CREATED", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReseniaDTO.class)))
    @PostMapping("/crear/usuario/{idUsuario}/libro/{idLibro}")
    public ResponseEntity<ReseniaDTO> crearResenia(@RequestBody ReseniaCreateDTO resenia, @PathVariable int idUsuario,
            @PathVariable int idLibro) {
        return new ResponseEntity<>(reseniaService.crearResenia(resenia, idLibro, idUsuario), HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todas las reseñas paginadas", description = "Devuelve una página con todas las reseñas disponibles, según el número de página solicitado.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @GetMapping("/listar/todas/{numeroPagina}")
    public ResponseEntity<Page<ReseniaDTO>> listarResenias(@PathVariable int numeroPagina) {
        return new ResponseEntity<>(reseniaService.listarResenias(numeroPagina), HttpStatus.OK);
    }

    @Operation(summary = "Listar reseñas de un libro paginadas", description = "Devuelve una página con las reseñas correspondientes a un libro específico según el número de página.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @GetMapping("/listar/{id_libro}/pag/{numeroPagina}")
    public ResponseEntity<Page<ReseniaDTO>> listarReseniasUnLibro(@PathVariable int id_libro,
            @PathVariable int numeroPagina) {
        return new ResponseEntity<>(reseniaService.listarReseniasByLibro(id_libro, numeroPagina), HttpStatus.OK);
    }

    @Operation(summary = "Listar reseñas de un usuario paginadas", description = "Devuelve una página con las reseñas realizadas por un usuario específico según el número de página.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @GetMapping("/listar/usuario/{id}/pag/{numeroPagina}")
    public ResponseEntity<Page<ReseniaDTO>> listarReseniasByUser(@PathVariable int id, @PathVariable int numeroPagina) {
        return new ResponseEntity<>(reseniaService.listarReseniasByUsuario(id, numeroPagina), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar una reseña", description = "Elimina una reseña específica asociada a un usuario.")
    @ApiResponse(responseCode = "204", description = "No Content - Reseña eliminada exitosamente", content = @Content(mediaType = "text/plain"))
    @DeleteMapping("/eliminar/usuario/{idUsuario}/resenia/{idResenia}")
    public ResponseEntity<String> eliminarResenia(@PathVariable int idResenia, @PathVariable int idUsuario) {
        reseniaService.eliminarResenia(idResenia, idUsuario);
        return new ResponseEntity<>("Resenia eliminada exitosamente", HttpStatus.NO_CONTENT);
    }
}
