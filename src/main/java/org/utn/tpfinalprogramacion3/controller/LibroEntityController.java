package org.utn.tpfinalprogramacion3.controller;

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

    @PostMapping("/crear/{idAutor}/{idGenero}")
    public ResponseEntity<LibroDTO> crearLibro(@RequestBody LibroDTO libro, @PathVariable int idAutor,
                                               @PathVariable int idGenero) {
        if (this.libroService.crearLibro(libro, idAutor, idGenero).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(libro, HttpStatus.CREATED);
    }

    @GetMapping("/listar/{numeroPagina}")
    public ResponseEntity<Page<LibroDTO>> obtenerLibros(@PathVariable int numeroPagina) {
        return new ResponseEntity<>(libroService.getAllLibros(numeroPagina), HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/{idLibro}/agregar_genero/{idGenero}")
    public ResponseEntity<String> AgregarGeneroALibro(@PathVariable int idLibro, @PathVariable int idGenero) {
        return new ResponseEntity<>(libroService.agregarGeneroALibro(idLibro, idGenero), HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/{idLibro}/agregar_autor/{idAutor}")
    public ResponseEntity<String> AgregarAutorALibro(@PathVariable int idLibro, @PathVariable int idAutor) {
        return new ResponseEntity<>(libroService.agregarAutorALibro(idLibro, idAutor), HttpStatus.CREATED);
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/{idLibro}/eliminar_autor/{idAutor}")
    public ResponseEntity<String> EliminarAutorALibro(@PathVariable int idLibro, @PathVariable int idAutor) {
        return new ResponseEntity<>(libroService.eliminarAutorALibro(idLibro, idAutor), HttpStatus.NO_CONTENT);
    }

    @PostMapping("/openlibrary/{generoId}")
    public ResponseEntity<LibroDTO> crearLibroConGenero(
            @RequestParam String titulo,
            @PathVariable Integer generoId) {

        LibroDTO libroDTO = new LibroDTO();
        libroDTO.setTitulo(titulo);

        LibroDTO creado = libroService.crearLibro2(libroDTO, generoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

}

