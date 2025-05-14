package org.utn.tpfinalprogramacion3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.utn.tpfinalprogramacion3.dtos.GeneroDTO;
import org.utn.tpfinalprogramacion3.repository.GeneroRepository;
import org.utn.tpfinalprogramacion3.services.GeneroService;

public class GeneroEntityController {
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


}
