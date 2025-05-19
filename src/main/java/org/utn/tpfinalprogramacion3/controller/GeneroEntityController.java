package org.utn.tpfinalprogramacion3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.dtos.GeneroDTO;
import org.utn.tpfinalprogramacion3.entities.GeneroEntity;
import org.utn.tpfinalprogramacion3.repository.GeneroRepository;
import org.utn.tpfinalprogramacion3.services.GeneroService;

import java.util.List;

@RestController
@RequestMapping("/generos")
public class    GeneroEntityController {
    private GeneroRepository generoRepository;
    private GeneroService generoService;


    public GeneroEntityController(GeneroRepository generoRepository, GeneroService generoService) {
        this.generoRepository = generoRepository;
        this.generoService = generoService;
    }
    @PostMapping("/crear")
    public GeneroDTO crearGenero(@RequestBody GeneroDTO generoDTO) {
        return generoService.Save(generoDTO);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<GeneroEntity>> listarGeneros() {
        return ResponseEntity.ok(generoService.findAll());
    }




}
