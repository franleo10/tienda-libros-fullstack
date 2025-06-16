package org.utn.tpfinalprogramacion3.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.dtos.AgregarLibroDTO;
import org.utn.tpfinalprogramacion3.dtos.BibliotecaDTO;
import org.utn.tpfinalprogramacion3.dtos.LibroBibliotecaDTO;
import org.utn.tpfinalprogramacion3.services.BibliotecaService;

import java.util.List;

@RestController
@RequestMapping("/biblioteca")
@RequiredArgsConstructor
public class BibliotecaEntityController {

    private final BibliotecaService bibliotecaService;

    @PostMapping("/agregar-libro/{idUsuario}")
    public ResponseEntity<String> agregarLibro(@PathVariable int idUsuario, @RequestBody AgregarLibroDTO dto) {

        dto.setIdUsuario(idUsuario);
        bibliotecaService.agregarLibro(dto);
        return ResponseEntity.ok("Libro agregado a la biblioteca del usuario");
    }



    @GetMapping("/listar")
    public ResponseEntity<List<BibliotecaDTO>> getAll() {
        return ResponseEntity.ok(bibliotecaService.findAll());
    }

    @GetMapping("/listar/{idUsuario}")
    public ResponseEntity<BibliotecaDTO> getByUsuario(@PathVariable int idUsuario) {
        BibliotecaDTO dto = bibliotecaService.getByUsuarioId(idUsuario);
        return ResponseEntity.ok(dto);
    }



    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> deleteBiblioteca(@PathVariable int id) {
        bibliotecaService.deleteById(id);
        return ResponseEntity.ok("Biblioteca eliminada correctamente.");
    }


    @GetMapping("/favoritos/{idUsuario}")
    public ResponseEntity<List<LibroBibliotecaDTO>> getLibrosFavoritos(@PathVariable int idUsuario) {
        List<LibroBibliotecaDTO> favoritos = bibliotecaService.getLibrosFavoritos(idUsuario);
        return ResponseEntity.ok(favoritos);
    }

    @PostMapping("/favoritos/{idUsuario}/{idLibro}")
    public ResponseEntity<String> marcarLibroFavorito(@PathVariable int idUsuario, @PathVariable int idLibro) {
        bibliotecaService.marcarLibroFavorito(idUsuario, idLibro);
        return ResponseEntity.ok("Libro marcado como favorito!!");
    }

    @DeleteMapping("/favoritos/{idUsuario}/{idLibro}")
    public ResponseEntity<String> desmarcarLibroFavorito(@PathVariable int idUsuario, @PathVariable int idLibro) {
        bibliotecaService.desmarcarLibroFavorito(idUsuario, idLibro);
        return ResponseEntity.ok("Libro desmarcado como favorito!");
    }


}
