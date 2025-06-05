package org.utn.tpfinalprogramacion3.security.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.enums.Rol;
import org.utn.tpfinalprogramacion3.repository.UsuarioRepository;
import org.utn.tpfinalprogramacion3.security.dto.AuthRequest;
import org.utn.tpfinalprogramacion3.security.dto.AuthResponse;
import org.utn.tpfinalprogramacion3.security.dto.RegisterRequest;
import org.utn.tpfinalprogramacion3.security.entities.CredencialEntity;
import org.utn.tpfinalprogramacion3.security.entities.RoleEntity;
import org.utn.tpfinalprogramacion3.security.repositories.CredentialsRepository;
import org.utn.tpfinalprogramacion3.security.repositories.RoleRepository;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    private final CredentialsRepository credentialsRepository;
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;



    public AuthService(CredentialsRepository credentialsRepository, AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService, RoleRepository roleRepository) {
        this.credentialsRepository = credentialsRepository;
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.roleRepository = roleRepository;
    }

    //Utiliza la clase AuthenticationManager para autenticar el usuario.
    public UserDetails authenticate(AuthRequest input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.username(),
                        input.password()
                )
        );
        return credentialsRepository.findByEmail(input.username()).orElseThrow();
    }

    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.email()) || usuarioRepository.existsByNombre(request.nombre())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre o email");
        }

        UsuarioEntity usuario = UsuarioEntity.builder()
                .nombre(request.nombre())
                .edad(request.edad())
                .email(request.email())
                .roles(Rol.USUARIO)
                .build();



        usuarioRepository.save(usuario);

        CredencialEntity credencial = CredencialEntity.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .usuario(usuario)
                .build();

        RoleEntity rolUsuario = roleRepository.findByRole(Rol.USUARIO)
                .orElseThrow(() -> new RuntimeException("Rol USUARIO no encontrado"));

        credencial.setRoles(new HashSet<>(Set.of(rolUsuario)));


        credentialsRepository.save(credencial);

        usuario.setCredencialEntity(credencial);
        usuarioRepository.save(usuario); // por si es necesario actualizar la relación bidireccional

        String jwt = jwtService.generateToken(credencial);
        return new AuthResponse(jwt);
    }

}