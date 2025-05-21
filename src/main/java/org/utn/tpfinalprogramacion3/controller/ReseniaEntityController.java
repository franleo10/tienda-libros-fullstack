package org.utn.tpfinalprogramacion3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.dtos.ReseniaCreateDTO;
import org.utn.tpfinalprogramacion3.dtos.ReseniaDTO;
import org.utn.tpfinalprogramacion3.repository.ReseniaRepository;
import org.utn.tpfinalprogramacion3.services.ReseniaService;

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



}
