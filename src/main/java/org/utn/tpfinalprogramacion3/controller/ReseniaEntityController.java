package org.utn.tpfinalprogramacion3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<ReseniaDTO> crearResenia(@RequestBody ReseniaDTO resenia) {
        reseniaService.crearResenia(resenia);
        return new ResponseEntity<>(resenia, HttpStatus.CREATED);
    }



}
