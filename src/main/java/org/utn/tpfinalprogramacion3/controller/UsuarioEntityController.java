package org.utn.tpfinalprogramacion3.controller;

import org.apache.catalina.connector.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
    public ResponseEntity<UsuarioCreateDTO> createUsuario(@RequestBody UsuarioCreateDTO usuarioDTO) {
        return new ResponseEntity<>(usuarioService.createUsuario(usuarioDTO), HttpStatus.CREATED);
    }

    @GetMapping("/listar/{numeroPagina}")
    public ResponseEntity<Page<UsuarioCreateDTO>> listarUsuarios(@PathVariable int numeroPagina) {
        return new ResponseEntity<>(usuarioService.findAll(numeroPagina), HttpStatus.OK);
    }

    @GetMapping("/listar/inactivos/{numeroPagina}")
    public ResponseEntity<Page<UsuarioCreateDTO>> listarUsuariosInactivos(@PathVariable int numeroPagina) {
        return new ResponseEntity<>(usuarioService.findAllInactivos(numeroPagina), HttpStatus.OK);
    }

    @GetMapping("/buscar_nombre")
    public ResponseEntity<UsuarioCreateDTO> buscarNombre(@RequestParam String nombre) {
        return new ResponseEntity<>(usuarioService.findByNombre(nombre), HttpStatus.OK);
    }

    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable int id) {
        return new ResponseEntity<>(usuarioService.delete(id), HttpStatus.NO_CONTENT);
    }

    @PutMapping("/baja/{id}")
    public ResponseEntity<String> darDeBajaUsuario(@PathVariable int id) {
        return new ResponseEntity<>(usuarioService.bajaUsuario(id), HttpStatus.NO_CONTENT);
    }

    @PutMapping("/alta/{id}")
    public ResponseEntity<String> darDeAltaUsuario(@PathVariable int id) {
        return new ResponseEntity<>(usuarioService.altaUsuario(id), HttpStatus.OK);
    }

    // Para que esta esto aca?
    @GetMapping("/verificar_nombre/{nombre}")
    public ResponseEntity<String> verificarUsuarioPorNombre(@PathVariable String nombre) {
        String mensaje = usuarioService.verificar_nombre(nombre);
        return ResponseEntity.ok(mensaje);
    }

}
