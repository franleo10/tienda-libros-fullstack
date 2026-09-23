package org.utn.tpfinalprogramacion3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.dtos.CarritoDTO;
import org.utn.tpfinalprogramacion3.dtos.CarritoDTO2;
import org.utn.tpfinalprogramacion3.entities.CarritoEntity;
import org.utn.tpfinalprogramacion3.services.CarritoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carrito")

public class CarritoEntityController {
    @Autowired
    private CarritoService carritoService;

    public CarritoEntityController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @Operation(summary = "Agregar libro al carrito", description = "Agrega un libro específico al carrito de compras identificado por su ID.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @PostMapping("/agregar-libro/{idCarrito}/{idLibro}")
    public ResponseEntity<String> agregarLibro(
            @PathVariable Integer idCarrito,
            @PathVariable Integer idLibro) {

        return new ResponseEntity<>(carritoService.agregarLibro(idCarrito, idLibro), HttpStatus.OK);
    }

    @Operation(summary = "Agregar libro al carrito del usuario autenticado", description = "Agrega un libro específico al carrito de compras del usuario actualmente autenticado.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @PostMapping("/libro/{idLibro}")
    public ResponseEntity<String> agregarLibroAMiCarrito(@PathVariable Integer idLibro) {
        return ResponseEntity.ok(carritoService.agregarLibroAMiCarrito(idLibro));
    }

    @Operation(summary = "Listar todos los carritos", description = "Obtiene la lista completa de carritos disponibles.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CarritoDTO.class))))
    @GetMapping("/listar")
    public ResponseEntity<List<CarritoDTO>> obtenerTodos() {
        List<CarritoDTO> carritos = carritoService.listarTodos();
        return ResponseEntity.ok(carritos);
    }

    @Operation(summary = "Obtener carrito por ID", description = "Busca un carrito específico por su ID y lo retorna. Si no se encuentra, devuelve 404 Not Found.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarritoEntity.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    })
    @GetMapping("/id/{id_carrito}")
    public ResponseEntity<CarritoEntity> obtenerPorId(@PathVariable("id_carrito") Integer id_carrito) {
        return carritoService.buscarPorId(id_carrito)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar carrito", description = "Elimina un carrito específico según su ID.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        carritoService.eliminar(id);
        return ResponseEntity.ok("Carrito eliminado correctamente.");
    }

    @Operation(summary = "Obtener carrito por ID de usuario", description = "Obtiene el carrito asociado a un usuario específico con los precios actualizados. Retorna 404 si no existe.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarritoEntity.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)
    })
    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<CarritoEntity> obtenerPorIdUsuario(@PathVariable("id_usuario") Integer idUsuario) {
        Optional<CarritoEntity> carritoOpt = carritoService.obtenerCarritoConPrecioActualizadoPorUsuario(idUsuario);

        if (carritoOpt.isPresent()) {
            return new ResponseEntity<>(carritoOpt.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Obtener carrito DTO por usuario", description = "Recupera el carrito del usuario especificado en formato DTO.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarritoDTO2.class)))
    @GetMapping("/listar/usuario/{idUsuario}")
    public ResponseEntity<CarritoDTO2> obtenerCarritoPorUsuario(@PathVariable Integer idUsuario) {
        return new ResponseEntity<>(carritoService.MostrarDTOcarritoPorUsuario(idUsuario), HttpStatus.OK);
    }

    @Operation(summary = "Obtener carrito DTO del usuario autenticado", description = "Recupera el carrito en formato DTO del usuario actualmente autenticado.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarritoDTO2.class)))
    @GetMapping("/listar/usuario")
    public ResponseEntity<CarritoDTO2> obtenerCarritoDelUsuarioLogueado() {
        return new ResponseEntity<>(carritoService.MostrarDTOcarrito(), HttpStatus.OK);
    }

    @Operation(summary = "Eliminar libro del carrito", description = "Elimina un libro específico del carrito del usuario.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @PutMapping("/eliminar-libro/{idLibro}")
    public ResponseEntity<String> eliminarLibroCarrito(@PathVariable Integer idLibro) {
        return new ResponseEntity<>(carritoService.eliminarLibroDelCarrito(idLibro), HttpStatus.OK);
    }

}
