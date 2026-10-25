package org.utn.tpfinalprogramacion3.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.utn.tpfinalprogramacion3.dtos.CarritoDTO2;
import org.utn.tpfinalprogramacion3.dtos.LibroCarritoDTO;
import org.utn.tpfinalprogramacion3.entities.BibliotecaEntity;
import org.utn.tpfinalprogramacion3.entities.CarritoEntity;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.repository.BibliotecaRepository;
import org.utn.tpfinalprogramacion3.repository.CarritoRepository;
import org.utn.tpfinalprogramacion3.repository.LibroRepository;
import org.utn.tpfinalprogramacion3.repository.UsuarioRepository;
import org.utn.tpfinalprogramacion3.security.entities.CredencialEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CarritoServiceTest {

    @AfterEach
    void limpiarSesion() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void mostrarCarritoIncluyeIdYPortadaDeCadaLibroYElTotal() {
        CarritoRepository carritos = mock(CarritoRepository.class);
        CarritoService service = new CarritoService(
                carritos,
                mock(LibroRepository.class),
                mock(UsuarioRepository.class),
                new ModelMapper(),
                mock(BibliotecaRepository.class)
        );
        iniciarSesion(7L);

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(7);
        usuario.setNombre("Ana");
        usuario.setEmail("ana@example.com");

        CarritoEntity carrito = new CarritoEntity();
        carrito.setIdCarrito(3);
        carrito.setUsuario(usuario);
        carrito.setLibros(new ArrayList<>(List.of(
                libro(12, "Rayuela", 12500F, "https://covers.openlibrary.org/b/id/1-M.jpg"),
                libro(15, "Ficciones", 9000F, null)
        )));

        when(carritos.findByUsuarioId(7)).thenReturn(Optional.of(carrito));

        CarritoDTO2 dto = service.MostrarDTOcarrito();

        assertEquals(3, dto.getIdCarrito());
        assertEquals(21500.0, dto.getPrecio());
        assertEquals(2, dto.getLibros().size());
        LibroCarritoDTO rayuela = dto.getLibros().get(0);
        assertEquals(12, rayuela.getIdLibro());
        assertEquals("Rayuela", rayuela.getTitulo());
        assertEquals("https://covers.openlibrary.org/b/id/1-M.jpg", rayuela.getUrlPortada());
        assertEquals(15, dto.getLibros().get(1).getIdLibro());
    }

    @Test
    void noSePuedeAgregarAlCarritoUnLibroQueYaEstaEnLaBiblioteca() {
        CarritoRepository carritos = mock(CarritoRepository.class);
        LibroRepository libros = mock(LibroRepository.class);
        BibliotecaRepository bibliotecas = mock(BibliotecaRepository.class);
        CarritoService service = new CarritoService(
                carritos, libros, mock(UsuarioRepository.class), new ModelMapper(), bibliotecas
        );
        iniciarSesion(7L);

        LibroEntity rayuela = libro(12, "Rayuela", 12500F, null);
        CarritoEntity carrito = new CarritoEntity();
        carrito.setLibros(new ArrayList<>());
        BibliotecaEntity biblioteca = new BibliotecaEntity();
        biblioteca.setLibros(new HashSet<>(Set.of(rayuela)));

        when(carritos.findByUsuarioId(7)).thenReturn(Optional.of(carrito));
        when(libros.findById(12)).thenReturn(Optional.of(rayuela));
        when(bibliotecas.findByUsuarioId(7)).thenReturn(Optional.of(biblioteca));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.agregarLibroAMiCarrito(12));

        assertEquals("Ya tenés este libro en tu biblioteca", error.getMessage());
        verify(carritos, never()).save(any());
    }

    private void iniciarSesion(long idCredencial) {
        CredencialEntity credencial = new CredencialEntity();
        credencial.setId(idCredencial);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(credencial, null, List.of())
        );
    }

    private LibroEntity libro(int id, String titulo, float precio, String urlPortada) {
        LibroEntity libro = new LibroEntity();
        libro.setIdLibro(id);
        libro.setTitulo(titulo);
        libro.setPrecio(precio);
        libro.setUrlPortada(urlPortada);
        return libro;
    }
}
