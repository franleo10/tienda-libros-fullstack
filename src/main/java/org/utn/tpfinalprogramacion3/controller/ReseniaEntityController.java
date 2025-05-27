package org.utn.tpfinalprogramacion3.controller;

import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.dtos.ReseniaCreateDTO;
import org.utn.tpfinalprogramacion3.dtos.ReseniaDTO;
import org.utn.tpfinalprogramacion3.repository.ReseniaRepository;
import org.utn.tpfinalprogramacion3.services.ReseniaService;

import java.util.List;

@RestController
@RequestMapping("/resenias")
public class ReseniaEntityController {

    private final ReseniaService reseniaService;

    @Autowired
    public ReseniaEntityController(ReseniaService reseniaService) {
        this.reseniaService = reseniaService;
    }
    @PostMapping("/crear/usuario/{idUsuario}/libro/{idLibro}")
    public ResponseEntity<ReseniaDTO> crearResenia(@RequestBody ReseniaCreateDTO resenia, @PathVariable int idUsuario, @PathVariable int idLibro) {
        return new ResponseEntity<>(reseniaService.crearResenia(resenia, idLibro, idUsuario), HttpStatus.CREATED);
    }

    @GetMapping("/listar/todas/{numeroPagina}")
    public ResponseEntity<Page<ReseniaDTO>> listarResenias(@PathVariable int numeroPagina) {
        return new ResponseEntity<>(reseniaService.listarResenias(numeroPagina), HttpStatus.OK);
    }

    @GetMapping("/listar/{id_libro}/pag/{NumeroPagina}")
    public ResponseEntity<Page<ReseniaDTO>> listarReseniasUnLibro(@PathVariable int id_libro, @PathVariable int numeroPagina) {
        return new ResponseEntity<>(reseniaService.listarReseniasByLibro(id_libro, numeroPagina), HttpStatus.OK);
    }

    @GetMapping("/listar/usuario/{id}/pag/{numeroPagina}")
    public ResponseEntity<Page<ReseniaDTO>> listarReseniasByUser(@PathVariable int id, @PathVariable int numeroPagina) {
        return new ResponseEntity<>(reseniaService.listarReseniasByUsuario(id, numeroPagina), HttpStatus.OK);
    }

    @DeleteMapping("/eliminar/usuario/{idUsuario}/resenia/{idResenia}")
    public ResponseEntity<String> eliminarResenia(@PathVariable int idResenia, @PathVariable int idUsuario) {
        reseniaService.eliminarResenia(idResenia, idUsuario);
        return new ResponseEntity<>("Resenia eliminada exitosamente", HttpStatus.NO_CONTENT);
    }
}
