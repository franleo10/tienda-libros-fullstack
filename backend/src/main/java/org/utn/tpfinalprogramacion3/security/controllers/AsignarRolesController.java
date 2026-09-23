package org.utn.tpfinalprogramacion3.security.controllers;



import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.enums.Rol;
import org.utn.tpfinalprogramacion3.security.services.AsignarRolService;


@RestController
@RequestMapping("/admin/roles")
@RequiredArgsConstructor
public class AsignarRolesController {

    private final AsignarRolService rolAsignacionService;

    @PostMapping("/asignar")
    public String asignarRol(@RequestParam String email, @RequestParam Rol rol) {
        return rolAsignacionService.asignarRol(email, rol);
    }
}

