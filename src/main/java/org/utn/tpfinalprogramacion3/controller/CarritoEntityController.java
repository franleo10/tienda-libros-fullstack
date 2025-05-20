package org.utn.tpfinalprogramacion3.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.dtos.CarritoDTO;
import org.utn.tpfinalprogramacion3.entities.CarritoEntity;
import org.utn.tpfinalprogramacion3.services.CarritoService;

import java.util.List;

@RestController
@RequestMapping("/carrito")

public class CarritoEntityController {
    @Autowired
    private CarritoService carritoService;

    public CarritoEntityController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }


    @PostMapping("/crear/{id_usuario}")
    public ResponseEntity<CarritoDTO>crearCarrito(@RequestBody CarritoDTO carritoDTO, @PathVariable Integer id_usuario) {
        if(this.carritoService.createCarrito(carritoDTO,id_usuario).isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(carritoDTO, HttpStatus.CREATED);
    }


    @PostMapping("/agregar-libro/{idCarrito}/{idLibro}")
    public ResponseEntity<CarritoEntity> agregarLibro(
            @PathVariable Integer idCarrito,
            @PathVariable Integer idLibro) {

        CarritoEntity actualizado = carritoService.agregarLibro(idCarrito, idLibro);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/usuario/{id_usuario}")
    public ResponseEntity<CarritoEntity> obtenerPorIdUsuario(@PathVariable("id_usuario") Integer idUsuario) {
        return carritoService.buscarPorIdUsuario(idUsuario)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }



    @GetMapping("/listar")
    public ResponseEntity<List<CarritoEntity>> obtenerTodos() {
        List<CarritoEntity> carritos = carritoService.listarTodos();
        return ResponseEntity.ok(carritos);
    }

    @GetMapping("/{id_carrito}")
    public ResponseEntity<CarritoEntity> obtenerPorId(@PathVariable Integer id) {
        return carritoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        carritoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
