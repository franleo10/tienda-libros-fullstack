package org.utn.tpfinalprogramacion3.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.utn.tpfinalprogramacion3.dtos.UsuarioAdminDTO;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.enums.Rol;
import org.utn.tpfinalprogramacion3.repository.UsuarioRepository;
import org.utn.tpfinalprogramacion3.security.entities.CredencialEntity;
import org.utn.tpfinalprogramacion3.security.entities.RoleEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsuarioServiceTest {

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void listarParaAdminNormalizaFiltrosYDevuelveRolYEstado() {
        UsuarioRepository repository = mock(UsuarioRepository.class);
        UsuarioService service = new UsuarioService(repository, new ModelMapper());

        UsuarioEntity admin = usuario(1, "Ana", Rol.ADMINISTRADOR);
        UsuarioEntity baneado = usuario(2, "Beto", Rol.USUARIO);
        baneado.setActivo(false);

        when(repository.buscarParaAdmin("ana", null, true, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(admin, baneado), PageRequest.of(0, 10), 2));

        Page<UsuarioAdminDTO> pagina = service.listarParaAdmin(0, "  ana ", Rol.ADMINISTRADOR, null);

        assertEquals(2, pagina.getTotalElements());
        UsuarioAdminDTO primero = pagina.getContent().get(0);
        assertEquals(1, primero.getId());
        assertEquals("Ana", primero.getNombre());
        assertEquals("ana@example.com", primero.getEmail());
        assertEquals(Rol.ADMINISTRADOR, primero.getRol());
        assertTrue(primero.isActivo());
        assertEquals(Rol.USUARIO, pagina.getContent().get(1).getRol());
        assertFalse(pagina.getContent().get(1).isActivo());
    }

    @Test
    void listarParaAdminIgnoraBusquedaVaciaYRolNulo() {
        UsuarioRepository repository = mock(UsuarioRepository.class);
        UsuarioService service = new UsuarioService(repository, new ModelMapper());

        when(repository.buscarParaAdmin(null, false, null, PageRequest.of(1, 10)))
                .thenReturn(Page.empty(PageRequest.of(1, 10)));

        Page<UsuarioAdminDTO> pagina = service.listarParaAdmin(1, "   ", null, false);

        assertTrue(pagina.isEmpty());
    }

    @Test
    void bajaUsuarioMarcaAlUsuarioComoInactivo() {
        UsuarioRepository repository = mock(UsuarioRepository.class);
        UsuarioService service = new UsuarioService(repository, new ModelMapper());
        loguearComo(usuario(1, "Ana", Rol.ADMINISTRADOR));

        UsuarioEntity objetivo = usuario(2, "Beto", Rol.USUARIO);
        when(repository.existsById(2)).thenReturn(true);
        when(repository.findById(2)).thenReturn(Optional.of(objetivo));

        service.bajaUsuario(2);

        assertFalse(objetivo.isActivo());
        verify(repository).save(objetivo);
    }

    @Test
    void bajaUsuarioNoPermiteQueElAdminSeDeDeBajaASiMismo() {
        UsuarioRepository repository = mock(UsuarioRepository.class);
        UsuarioService service = new UsuarioService(repository, new ModelMapper());
        UsuarioEntity admin = usuario(1, "Ana", Rol.ADMINISTRADOR);
        loguearComo(admin);

        when(repository.existsById(1)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.bajaUsuario(1));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertTrue(admin.isActivo());
        verify(repository, never()).save(any());
    }

    @Test
    void credencialDeUsuarioDadoDeBajaQuedaDeshabilitada() {
        UsuarioEntity usuario = usuario(2, "Beto", Rol.USUARIO);
        CredencialEntity credencial = usuario.getCredencialEntity();

        assertTrue(credencial.isEnabled());

        usuario.setActivo(false);

        assertFalse(credencial.isEnabled());
    }

    private static UsuarioEntity usuario(int id, String nombre, Rol rol) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setEmail(nombre.toLowerCase() + "@example.com");
        usuario.setActivo(true);

        CredencialEntity credencial = new CredencialEntity();
        credencial.setId((long) id);
        credencial.setEmail(usuario.getEmail());
        credencial.setUsuario(usuario);
        credencial.setRoles(Set.of(RoleEntity.builder().role(rol).build()));
        usuario.setCredencialEntity(credencial);
        return usuario;
    }

    private static void loguearComo(UsuarioEntity usuario) {
        CredencialEntity credencial = usuario.getCredencialEntity();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(credencial, null, List.of()));
    }
}
