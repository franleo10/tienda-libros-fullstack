package org.utn.tpfinalprogramacion3.security.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.utn.tpfinalprogramacion3.security.dto.ActualizarPerfilRequest;
import org.utn.tpfinalprogramacion3.security.dto.AuthResponse;
import org.utn.tpfinalprogramacion3.security.dto.CambiarPasswordRequest;
import org.utn.tpfinalprogramacion3.security.dto.PerfilDTO;
import org.utn.tpfinalprogramacion3.security.services.PerfilService;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class MeController {

    private final PerfilService perfilService;

    public MeController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping("/me/perfil")
    public PerfilDTO getProfile(Authentication authentication) {
        return perfilService.obtenerPerfil(authentication.getName());
    }

    @PutMapping("/me/perfil")
    public ResponseEntity<AuthResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody ActualizarPerfilRequest request
    ) {
        String token = perfilService.actualizarPerfil(authentication.getName(), request);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PutMapping("/me/password")
    public ResponseEntity<AuthResponse> changePassword(
            Authentication authentication,
            @Valid @RequestBody CambiarPasswordRequest request
    ) {
        String token = perfilService.cambiarPassword(authentication.getName(), request);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @GetMapping("/me/permisos")
    public Set<String> getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }
}
