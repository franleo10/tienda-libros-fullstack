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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carrito")

public class    CarritoEntityController {
    @Autowired
    private CarritoService carritoService;

    public CarritoEntityController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }


    @PostMapping("/agregar-libro/{idCarrito}/{idLibro}")
    public ResponseEntity<CarritoEntity> agregarLibro(
            @PathVariable Integer idCarrito,
            @PathVariable Integer idLibro) {

        CarritoEntity actualizado = carritoService.agregarLibro(idCarrito, idLibro);
        return ResponseEntity.ok(actualizado);
    }


    @GetMapping("/listar")
    public ResponseEntity<List<CarritoDTO>> obtenerTodos() {
        List<CarritoDTO> carritos = carritoService.listarTodos();
        return ResponseEntity.ok(carritos);
    }

    @GetMapping("/id/{id_carrito}")
    public ResponseEntity<CarritoEntity> obtenerPorId(@PathVariable("id_carrito") Integer id_carrito) {
        return carritoService.buscarPorId(id_carrito)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        carritoService.eliminar(id);
        return ResponseEntity.ok("Carrito eliminado correctamente.");
    }

    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<CarritoEntity> obtenerPorIdUsuario(@PathVariable("id_usuario") Integer idUsuario) {
        Optional<CarritoEntity> carritoOpt = carritoService.obtenerCarritoConPrecioActualizadoPorUsuario(idUsuario);

        if (carritoOpt.isPresent()) {
            return new ResponseEntity<>(carritoOpt.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/listar/usuario/{idUsuario}")
    public ResponseEntity<CarritoDTO2> obtenerCarritoPorUsuario(@PathVariable Integer idUsuario) {
        return carritoService.MostrarDTOcarrito(idUsuario);
    }




}
