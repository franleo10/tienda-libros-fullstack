package org.utn.tpfinalprogramacion3.controller;

import org.apache.coyote.Response;
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
    @PostMapping("/crear")
    public ResponseEntity<ReseniaDTO> crearResenia(@RequestBody ReseniaCreateDTO resenia) {
        return new ResponseEntity<>(reseniaService.crearResenia(resenia), HttpStatus.CREATED);
    }

    @GetMapping("/listar/todas")
    public ResponseEntity<List<ReseniaDTO>> listarResenias() {
        return new ResponseEntity<>(reseniaService.listarResenias(), HttpStatus.OK);
    }

    @GetMapping("/listar/{id_libro}")
    public ResponseEntity<List<ReseniaDTO>> listarReseniasUnLibro(@PathVariable int id_libro) {
        return new ResponseEntity<>(reseniaService.listarReseniasByLibro(id_libro), HttpStatus.OK);
    }

    @GetMapping("/listar/usuario/{id}")
    public ResponseEntity<List<ReseniaDTO>> listarReseniasByUser(@PathVariable int id) {
        return new ResponseEntity<>(reseniaService.listarReseniasByUsuario(id), HttpStatus.OK);
    }

    @DeleteMapping("/eliminar/usuario/{idUsuario}/resenia/{idResenia}")
    public ResponseEntity<String> eliminarResenia(@PathVariable int idResenia, @PathVariable int idUsuario) {
        reseniaService.eliminarResenia(idResenia, idUsuario);
        return new ResponseEntity<>("Resenia eliminada exitosamente", HttpStatus.NO_CONTENT);
    }
}
