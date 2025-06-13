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

    @PostMapping("/crear")
    public ResponseEntity<String> createAutor(@RequestBody AutorDTO autorDTO) {
        return new ResponseEntity<>(autorService.createAutor(autorDTO), HttpStatus.CREATED);
    }

    @GetMapping("/listar/{numeroPagina}")
    public ResponseEntity<Page<AutorDTO>> getAutores(@PathVariable int numeroPagina) {
        return new ResponseEntity<>(autorService.listarAutores(numeroPagina), HttpStatus.OK);
    }

    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> deleteAutor(@PathVariable int id) {
        return new ResponseEntity<>(autorService.borrarAutor(id), HttpStatus.NO_CONTENT);
    }

    @GetMapping("/listar/nombre/{nombre}/{numeroPagina}")
    public ResponseEntity<Page<AutorDTO>> getAutoresByName(@PathVariable String nombre, @PathVariable int numeroPagina) {
        return new ResponseEntity<>(autorService.listarPorNombre(nombre, numeroPagina), HttpStatus.OK);
    }

    @GetMapping("/nombre/{numeroPagina}")
    public ResponseEntity<Page<LibroDTO>> obtenerLibrosPorAutor(@RequestParam String nombre, @PathVariable int numeroPagina) {
        return new ResponseEntity<>(autorService.obtenerLibrosPorNombreAutor(nombre, numeroPagina), HttpStatus.OK);
    }

    @PutMapping("/actualizar-autor/{id}")
    public ResponseEntity<String> actualizarAutor(@PathVariable Integer id, @RequestBody AutorEntity nuevoAutor) {
        return new ResponseEntity<>(autorService.actualizarAutor(id, nuevoAutor), HttpStatus.OK);
    }


}
