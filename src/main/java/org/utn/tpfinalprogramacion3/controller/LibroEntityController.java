package org.utn.tpfinalprogramacion3.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.dtos.LibroDTO;
import org.utn.tpfinalprogramacion3.services.LibroService;

@RestController
@RequestMapping("/libro")
public class LibroEntityController {

private LibroService libroService;

public LibroEntityController(LibroService libroService) {
    this.libroService = libroService;
}
@PostMapping("/crear/{id}")
    public ResponseEntity<LibroDTO> crearLibro(@RequestBody LibroDTO libro, @PathVariable int idAutor) {
    if(this.libroService.crearLibro(libro,idAutor).isEmpty()){
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(libro, HttpStatus.CREATED);
}



}
