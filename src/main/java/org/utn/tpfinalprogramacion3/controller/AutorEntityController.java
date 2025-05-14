package org.utn.tpfinalprogramacion3.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.dtos.AutorDTO;
import org.utn.tpfinalprogramacion3.entities.AutorEntity;
import org.utn.tpfinalprogramacion3.repository.AutorRepository;
import org.utn.tpfinalprogramacion3.services.AutorService;

import java.util.List;

@RestController
@RequestMapping("/autores")
public class AutorEntityController {

    private final AutorRepository autorRepository;
    private final AutorService autorService;
    @Autowired
    public AutorEntityController(AutorService autorService, AutorRepository autorRepository) {
        this.autorService = autorService;
        this.autorRepository = autorRepository;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> createAutor(@RequestBody AutorDTO autorDTO) {
        if(autorService.createAutor(autorDTO).isEmpty()){
            return new ResponseEntity<>("ERROR al crear autor, este ya existe en el sistema", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(autorDTO, HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<AutorEntity>> getAutores(){
        if(autorRepository.findAll().isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(autorRepository.findAll());
    }

    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> deleteAutor(@PathVariable int id){
        if(autorRepository.findById(id).isEmpty()){
            String mensaje = "No se encontro el autor con el id " + id;
            return new ResponseEntity<>(mensaje, HttpStatus.NOT_FOUND);
        }
        autorRepository.deleteById(id);
        return new ResponseEntity<>("Autor eliminado.", HttpStatus.OK);
    }

    @GetMapping("/listar/nombre/{nombre}")
    public ResponseEntity<List<AutorEntity>> getAutoresByName(@PathVariable String nombre){
        if(autorRepository.findAllByNombre(nombre).isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(autorRepository.findAllByNombre(nombre));
    }

}
