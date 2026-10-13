package org.utn.tpfinalprogramacion3.security.services;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.repository.UsuarioRepository;
import org.utn.tpfinalprogramacion3.security.dto.ActualizarPerfilRequest;
import org.utn.tpfinalprogramacion3.security.dto.CambiarPasswordRequest;
import org.utn.tpfinalprogramacion3.security.entities.CredencialEntity;
import org.utn.tpfinalprogramacion3.security.repositories.CredentialsRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PerfilServiceTest {

    @Test
    void actualizarPerfilActualizaDatosYCorreoDeAcceso() {
        CredentialsRepository credentials = mock(CredentialsRepository.class);
        UsuarioRepository users = mock(UsuarioRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);
        PerfilService service = new PerfilService(credentials, users, encoder, jwtService);

        UsuarioEntity user = new UsuarioEntity();
        user.setId(4);
        user.setNombre("Ana");
        user.setEdad(25);
        user.setEmail("ana@example.com");

        CredencialEntity credential = new CredencialEntity();
        credential.setId(9L);
        credential.setEmail("ana@example.com");
        credential.setUsuario(user);

        when(credentials.findByEmail("ana@example.com")).thenReturn(Optional.of(credential));
        when(users.findByNombre("Ana actualizada")).thenReturn(Optional.empty());
        when(users.findByEmail("nueva@example.com")).thenReturn(Optional.empty());
        when(credentials.findByEmail("nueva@example.com")).thenReturn(Optional.empty());
        when(jwtService.generateToken(credential)).thenReturn("refreshed-jwt");

        String token = service.actualizarPerfil(
                "ana@example.com",
                new ActualizarPerfilRequest(" Ana actualizada ", " nueva@example.com ", 26)
        );

        assertEquals("Ana actualizada", user.getNombre());
        assertEquals(26, user.getEdad());
        assertEquals("nueva@example.com", user.getEmail());
        assertEquals("nueva@example.com", credential.getEmail());
        assertEquals("refreshed-jwt", token);
        verify(users).save(user);
        verify(credentials).save(credential);
    }

    @Test
    void actualizarPerfilRechazaUnNombreUsadoPorOtraCuenta() {
        CredentialsRepository credentials = mock(CredentialsRepository.class);
        UsuarioRepository users = mock(UsuarioRepository.class);
        PerfilService service = new PerfilService(
                credentials,
                users,
                mock(PasswordEncoder.class),
                mock(JwtService.class)
        );

        UsuarioEntity user = new UsuarioEntity();
        user.setId(4);
        user.setEmail("ana@example.com");

        CredencialEntity credential = new CredencialEntity();
        credential.setId(9L);
        credential.setEmail("ana@example.com");
        credential.setUsuario(user);

        UsuarioEntity otherUser = new UsuarioEntity();
        otherUser.setId(5);
        when(credentials.findByEmail("ana@example.com")).thenReturn(Optional.of(credential));
        when(users.findByNombre("ocupado")).thenReturn(Optional.of(otherUser));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.actualizarPerfil(
                        "ana@example.com",
                        new ActualizarPerfilRequest("ocupado", "ana@example.com", 25)
                )
        );

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        assertEquals("El nombre de usuario que ingresó ya está ocupado.", error.getReason());
    }

    @Test
    void cambiarPasswordRechazaLaPasswordActualIncorrecta() {
        CredentialsRepository credentials = mock(CredentialsRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        PerfilService service = new PerfilService(
                credentials,
                mock(UsuarioRepository.class),
                encoder,
                mock(JwtService.class)
        );

        CredencialEntity credential = new CredencialEntity();
        credential.setPassword("encoded-current");
        when(credentials.findByEmail("ana@example.com")).thenReturn(Optional.of(credential));
        when(encoder.matches("wrong", "encoded-current")).thenReturn(false);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.cambiarPassword(
                        "ana@example.com",
                        new CambiarPasswordRequest("wrong", "new-password")
                )
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }

    @Test
    void cambiarPasswordGuardaHashYDevuelveTokenActualizado() {
        CredentialsRepository credentials = mock(CredentialsRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);
        PerfilService service = new PerfilService(
                credentials,
                mock(UsuarioRepository.class),
                encoder,
                jwtService
        );

        CredencialEntity credential = new CredencialEntity();
        credential.setPassword("encoded-current");
        when(credentials.findByEmail("ana@example.com")).thenReturn(Optional.of(credential));
        when(encoder.matches("current", "encoded-current")).thenReturn(true);
        when(encoder.matches("new-password", "encoded-current")).thenReturn(false);
        when(encoder.encode("new-password")).thenReturn("encoded-new");
        when(jwtService.generateToken(credential)).thenReturn("refreshed-jwt");

        String token = service.cambiarPassword(
                "ana@example.com",
                new CambiarPasswordRequest("current", "new-password")
        );

        assertEquals("encoded-new", credential.getPassword());
        assertEquals("refreshed-jwt", token);
        verify(credentials).save(credential);
    }
}
