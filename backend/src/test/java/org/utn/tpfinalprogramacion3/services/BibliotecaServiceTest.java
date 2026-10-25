package org.utn.tpfinalprogramacion3.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.utn.tpfinalprogramacion3.dtos.BibliotecaDTO;
import org.utn.tpfinalprogramacion3.dtos.LibroBibliotecaDTO;
import org.utn.tpfinalprogramacion3.entities.AutorEntity;
import org.utn.tpfinalprogramacion3.entities.BibliotecaEntity;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.repository.BibliotecaRepository;
import org.utn.tpfinalprogramacion3.repository.LibroRepository;
import org.utn.tpfinalprogramacion3.repository.UsuarioRepository;
import org.utn.tpfinalprogramacion3.security.entities.CredencialEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BibliotecaServiceTest {

    @AfterEach
    void limpiarSesion() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void bibliotecaDelUsuarioIncluyePortadaAutoresYFavoritosSinConsultarOpenLibrary() {
        UsuarioRepository usuarios = mock(UsuarioRepository.class);
        BibliotecaService service = new BibliotecaService(
                mock(BibliotecaRepository.class),
                usuarios,
                mock(LibroRepository.class)
        );

        CredencialEntity credencial = new CredencialEntity();
        credencial.setId(7L);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(credencial, null, List.of())
        );

        AutorEntity autor = new AutorEntity("Julio", "Cortázar");
        LibroEntity rayuela = libro(12, "Rayuela", autor);
        rayuela.setUrlPortada("https://covers.openlibrary.org/b/id/1-M.jpg");
        rayuela.setUrlPdf("https://openlibrary.org/works/OL1W");
        LibroEntity bestiario = libro(15, "Bestiario", autor);

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(7);
        usuario.setNombre("Ana");
        BibliotecaEntity biblioteca = new BibliotecaEntity();
        biblioteca.setUsuario(usuario);
        biblioteca.setLibros(new HashSet<>(Set.of(rayuela, bestiario)));
        biblioteca.setLibrosFavoritos(new HashSet<>(Set.of(rayuela)));
        usuario.setBiblioteca(biblioteca);

        when(usuarios.findById(7)).thenReturn(Optional.of(usuario));

        BibliotecaDTO dto = service.getByUsuarioId();

        assertEquals(2, dto.getLibros().size());
        LibroBibliotecaDTO libroRayuela = buscar(dto, 12);
        assertEquals("Rayuela", libroRayuela.getTitulo());
        assertEquals("https://covers.openlibrary.org/b/id/1-M.jpg", libroRayuela.getUrlPortada());
        assertEquals("https://openlibrary.org/works/OL1W", libroRayuela.getUrlPdf());
        assertEquals("Cortázar", libroRayuela.getAutores().get(0).getApellido());
        assertTrue(libroRayuela.isEsFavorito());
        assertFalse(buscar(dto, 15).isEsFavorito());
    }

    private LibroEntity libro(int id, String titulo, AutorEntity autor) {
        LibroEntity libro = new LibroEntity();
        libro.setIdLibro(id);
        libro.setTitulo(titulo);
        libro.setPrecio(1000F);
        libro.setAutores(Set.of(autor));
        return libro;
    }

    private LibroBibliotecaDTO buscar(BibliotecaDTO dto, int idLibro) {
        return dto.getLibros().stream()
                .filter(libro -> libro.getIdLibro() == idLibro)
                .findFirst()
                .orElseThrow();
    }
}
