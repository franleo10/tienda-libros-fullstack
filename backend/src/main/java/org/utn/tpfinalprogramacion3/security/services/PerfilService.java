package org.utn.tpfinalprogramacion3.security.services;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.repository.UsuarioRepository;
import org.utn.tpfinalprogramacion3.security.dto.ActualizarPerfilRequest;
import org.utn.tpfinalprogramacion3.security.dto.CambiarPasswordRequest;
import org.utn.tpfinalprogramacion3.security.dto.PerfilDTO;
import org.utn.tpfinalprogramacion3.security.entities.CredencialEntity;
import org.utn.tpfinalprogramacion3.security.repositories.CredentialsRepository;

@Service
public class PerfilService {

    private final CredentialsRepository credentialsRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public PerfilService(
            CredentialsRepository credentialsRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.credentialsRepository = credentialsRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public PerfilDTO obtenerPerfil(String emailAutenticado) {
        return toDTO(obtenerCredencial(emailAutenticado).getUsuario());
    }

    @Transactional
    public String actualizarPerfil(String emailAutenticado, ActualizarPerfilRequest request) {
        CredencialEntity credencial = obtenerCredencial(emailAutenticado);
        UsuarioEntity usuario = credencial.getUsuario();
        String nuevoNombre = request.nombre().trim();
        String nuevoEmail = request.email().trim();

        usuarioRepository.findByNombre(nuevoNombre)
                .filter(otroUsuario -> otroUsuario.getId() != usuario.getId())
                .ifPresent(otroUsuario -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "El nombre de usuario que ingresó ya está ocupado."
                    );
                });

        usuarioRepository.findByEmail(nuevoEmail)
                .filter(otroUsuario -> otroUsuario.getId() != usuario.getId())
                .ifPresent(otroUsuario -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese email ya está registrado.");
                });

        credentialsRepository.findByEmail(nuevoEmail)
                .filter(otraCredencial -> !otraCredencial.getId().equals(credencial.getId()))
                .ifPresent(otraCredencial -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese email ya está registrado.");
                });

        usuario.setNombre(nuevoNombre);
        usuario.setEdad(request.edad());
        usuario.setEmail(nuevoEmail);
        credencial.setEmail(nuevoEmail);
        usuarioRepository.save(usuario);
        credentialsRepository.save(credencial);

        return jwtService.generateToken(credencial);
    }

    @Transactional
    public String cambiarPassword(String emailAutenticado, CambiarPasswordRequest request) {
        CredencialEntity credencial = obtenerCredencial(emailAutenticado);
        if (!passwordEncoder.matches(request.passwordActual(), credencial.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual no es correcta.");
        }
        if (passwordEncoder.matches(request.passwordNueva(), credencial.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña nueva debe ser diferente.");
        }

        credencial.setPassword(passwordEncoder.encode(request.passwordNueva()));
        credentialsRepository.save(credencial);
        return jwtService.generateToken(credencial);
    }

    private CredencialEntity obtenerCredencial(String email) {
        return credentialsRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la cuenta."));
    }

    private PerfilDTO toDTO(UsuarioEntity usuario) {
        return new PerfilDTO(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getEdad());
    }
}
