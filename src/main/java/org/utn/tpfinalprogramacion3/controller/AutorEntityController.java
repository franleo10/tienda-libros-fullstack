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
    public ResponseEntity<AutorDTO> createAutor(@RequestBody AutorDTO autorDTO) {
        return autorService.createAutor(autorDTO);
    }

    @GetMapping("/listar/{numeroPagina}")
    public ResponseEntity<Page<AutorDTO>> getAutores(@PathVariable int numeroPagina) {
        return new ResponseEntity<>(autorService.listarAutores(numeroPagina), HttpStatus.OK);
    }

    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> deleteAutor(@PathVariable int id) {
        return autorService.borrarAutor(id);
    }

    @GetMapping("/listar/nombre/{nombre}")
    public ResponseEntity<List<AutorEntity>> getAutoresByName(@PathVariable String nombre) {
        return autorService.listarPorNombre(nombre);
    }

    @GetMapping("/nombre")
    public ResponseEntity<List<LibroEntity>> obtenerLibrosPorAutor(@RequestParam String nombre) {
        List<LibroEntity> libros = autorService.obtenerLibrosPorNombreAutor(nombre);
        return ResponseEntity.ok(libros);
    }

    @PutMapping("/actualizar-autor/{id}")
    public ResponseEntity<AutorEntity> actualizarAutor(@PathVariable Integer id, @RequestBody AutorEntity nuevoAutor) {
        AutorEntity actualizado = autorService.actualizarAutor(id, nuevoAutor);
        return ResponseEntity.ok(actualizado);
    }


}
