package org.utn.tpfinalprogramacion3.controller;

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

private LibroService libroService;

public LibroEntityController(LibroService libroService) {
    this.libroService = libroService;
}


@PostMapping("/crear/{idAutor}/{idGenero}")
    public ResponseEntity<LibroDTO> crearLibro(@RequestBody LibroDTO libro, @PathVariable int idAutor, @PathVariable int idGenero) {
    if(this.libroService.crearLibro(libro,idAutor,idGenero).isEmpty()){
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(libro, HttpStatus.CREATED);
}
@GetMapping("/listar")
public ResponseEntity<List<LibroEntity>> obtenerLibros() {
    try {
        List<LibroEntity> libros = libroService.getAllLibros();
        if (libros.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(libros);
    } catch (Exception e) {

        return ResponseEntity.status(500).body(null);
    }
}
    @CrossOrigin(origins = "*")
    @PutMapping("/{idLibro}/agregar_genero/{idGenero}")
    public ResponseEntity<?> AgregarGeneroALibro(@PathVariable int idLibro, @PathVariable int idGenero) {
       return libroService.agregarGeneroALibro(idLibro,idGenero);
    }
    @CrossOrigin(origins = "*")
    @PutMapping("/{idLibro}/agregar_autor/{idAutor}")
    public ResponseEntity<?>AgregarAutorALibro(@PathVariable int idLibro, @PathVariable int idAutor) {
    return libroService.agregarAutorALibro(idLibro, idAutor);
    }
    @CrossOrigin(origins = "*")
    @DeleteMapping("/{idLibro}/eliminar_autor/{idAutor}")
    public ResponseEntity<?>EliminarAutorALibro(@PathVariable int idLibro, @PathVariable int idAutor) {
    return libroService.eliminarAutorALibro(idLibro, idAutor);
    }





}
