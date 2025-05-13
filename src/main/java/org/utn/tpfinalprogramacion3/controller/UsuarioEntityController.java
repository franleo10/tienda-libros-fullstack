package org.utn.tpfinalprogramacion3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.utn.tpfinalprogramacion3.dtos.UsuarioCreateDTO;
import org.utn.tpfinalprogramacion3.services.UsuarioService;

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

}
