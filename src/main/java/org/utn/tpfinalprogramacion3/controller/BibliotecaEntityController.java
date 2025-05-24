package org.utn.tpfinalprogramacion3.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.dtos.AgregarLibroDTO;
import org.utn.tpfinalprogramacion3.dtos.BibliotecaDTO;
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
        return bibliotecaService.findById(idUsuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/eliminar/{idUsuario}/{idLibro}")
    public ResponseEntity<Void> delete(@PathVariable int idUsuario, @PathVariable int idLibro) {
        bibliotecaService.deleteById(idUsuario, idLibro);
        return ResponseEntity.noContent().build();
    }
}
