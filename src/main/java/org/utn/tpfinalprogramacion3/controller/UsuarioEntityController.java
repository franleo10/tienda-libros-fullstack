package org.utn.tpfinalprogramacion3.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.dtos.UsuarioCreateDTO;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.services.UsuarioService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuario")
public class UsuarioEntityController {

    private final UsuarioService usuarioService;
    public UsuarioEntityController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/crear")
    public UsuarioCreateDTO createUsuario(@RequestBody UsuarioCreateDTO usuarioDTO) {
        return usuarioService.createUsuario(usuarioDTO);
    }

    @GetMapping("/listar")
    public List<UsuarioEntity> listarUsuarios(){
        return usuarioService.findAll();
    }

    @GetMapping("/buscar_nombre")
    public Optional<UsuarioEntity> buscarNombre(@RequestParam String nombre){
        return usuarioService.findByNombre(nombre);
    }

    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable int id) {
        try {
            usuarioService.delete(id);
            return new ResponseEntity<>("Usuario eliminado con éxito", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/verificar_nombre/{nombre}")
    public ResponseEntity<String> verificarUsuarioPorNombre(@PathVariable String nombre) {
        String mensaje = usuarioService.verificar_nombre(nombre);
        return ResponseEntity.ok(mensaje);
    }



}
