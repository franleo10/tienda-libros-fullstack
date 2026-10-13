package org.utn.tpfinalprogramacion3.services;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.utn.tpfinalprogramacion3.Exceptions.AutorExistenteException;
import org.utn.tpfinalprogramacion3.dtos.AutorDTO;
import org.utn.tpfinalprogramacion3.dtos.AutorDetalleDTO;
import org.utn.tpfinalprogramacion3.entities.AutorEntity;
import org.utn.tpfinalprogramacion3.entities.GeneroEntity;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.entities.ReseniaEntity;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.repository.AutorRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AutorServiceTest {

    @Test
    void crearAutorRecortaCamposAntesDeGuardar() {
        AutorRepository repository = mock(AutorRepository.class);
        AutorService service = new AutorService(repository, new ModelMapper());
        AutorDTO dto = autorDTO("  Julio ", " Cortázar  ");

        when(repository.existsByNombreIgnoreCaseAndApellidoIgnoreCase("Julio", "Cortázar"))
                .thenReturn(false);

        service.createAutor(dto);

        org.mockito.ArgumentCaptor<AutorEntity> captor =
                org.mockito.ArgumentCaptor.forClass(AutorEntity.class);
        verify(repository).save(captor.capture());
        assertEquals("Julio", captor.getValue().getNombre());
        assertEquals("Cortázar", captor.getValue().getApellido());
    }

    @Test
    void crearAutorInformaCuandoYaExisteSinDistinguirMayusculas() {
        AutorRepository repository = mock(AutorRepository.class);
        AutorService service = new AutorService(repository, new ModelMapper());
        AutorDTO dto = autorDTO("julio", "cortázar");

        when(repository.existsByNombreIgnoreCaseAndApellidoIgnoreCase("julio", "cortázar"))
                .thenReturn(true);

        assertThrows(AutorExistenteException.class, () -> service.createAutor(dto));
    }

    @Test
    void actualizarAutorRechazaElDuplicadoYNoGuardaCambios() {
        AutorRepository repository = mock(AutorRepository.class);
        AutorService service = new AutorService(repository, new ModelMapper());
        AutorEntity autorExistente = new AutorEntity();
        autorExistente.setIdAutor(3);
        AutorDTO dto = autorDTO("Julio", "Cortázar");

        when(repository.findByIdAutorAndActivoTrue(3)).thenReturn(Optional.of(autorExistente));
        when(repository.existsByNombreIgnoreCaseAndApellidoIgnoreCaseAndIdAutorNot(
                "Julio",
                "Cortázar",
                3
        )).thenReturn(true);

        assertThrows(AutorExistenteException.class, () -> service.actualizarAutor(3, dto));
    }

    @Test
    void actualizarAutorGuardaLosCamposRecortados() {
        AutorRepository repository = mock(AutorRepository.class);
        AutorService service = new AutorService(repository, new ModelMapper());
        AutorEntity autorExistente = new AutorEntity();
        autorExistente.setIdAutor(3);
        AutorDTO dto = autorDTO("  Julio ", " Cortázar  ");

        when(repository.findByIdAutorAndActivoTrue(3)).thenReturn(Optional.of(autorExistente));
        when(repository.existsByNombreIgnoreCaseAndApellidoIgnoreCaseAndIdAutorNot(
                "Julio",
                "Cortázar",
                3
        )).thenReturn(false);

        service.actualizarAutor(3, dto);

        verify(repository).save(autorExistente);
        assertEquals("Julio", autorExistente.getNombre());
        assertEquals("Cortázar", autorExistente.getApellido());
    }

    @Test
    void darDeBajaAutorConservaAutorYRelacionesConLibros() {
        AutorRepository repository = mock(AutorRepository.class);
        AutorService service = new AutorService(repository, new ModelMapper());
        AutorEntity autor = new AutorEntity();
        autor.setIdAutor(4);
        LibroEntity libro = new LibroEntity();
        libro.setAutores(new java.util.HashSet<>(Set.of(autor)));
        autor.setLibros(List.of(libro));
        when(repository.findById(4)).thenReturn(Optional.of(autor));

        service.darDeBajaAutor(4);

        assertEquals(false, autor.isActivo());
        assertTrue(libro.getAutores().contains(autor));
        verify(repository).save(autor);
    }

    @Test
    void darDeAltaAutorLoReactivaSinCambiarSusRelaciones() {
        AutorRepository repository = mock(AutorRepository.class);
        AutorService service = new AutorService(repository, new ModelMapper());
        AutorEntity autor = new AutorEntity();
        autor.setIdAutor(4);
        autor.setActivo(false);
        LibroEntity libro = new LibroEntity();
        libro.setAutores(new java.util.HashSet<>(Set.of(autor)));
        autor.setLibros(List.of(libro));
        when(repository.findById(4)).thenReturn(Optional.of(autor));

        service.darDeAltaAutor(4);

        assertTrue(autor.isActivo());
        assertTrue(libro.getAutores().contains(autor));
        verify(repository).save(autor);
    }

    @Test
    void detalleAutorIncluyeGeneroReseniasYCalificacionesDelLibro() {
        AutorRepository repository = mock(AutorRepository.class);
        AutorService service = new AutorService(repository, new ModelMapper());

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre("lectora");

        ReseniaEntity resenia = new ReseniaEntity();
        resenia.setIdResenia(7);
        resenia.setTexto("Una lectura inolvidable.");
        resenia.setCalificacion(new BigDecimal("4.5"));
        resenia.setUsuario(usuario);

        GeneroEntity genero = new GeneroEntity("Novela");

        LibroEntity libro = new LibroEntity();
        libro.setIdLibro(12);
        libro.setTitulo("Rayuela");
        libro.setSinopsis("Una novela experimental.");
        libro.setPrecio(12500F);
        libro.setFecha_lanzamiento(LocalDate.of(1963, 6, 28));
        libro.setGeneros(Set.of(genero));
        libro.setListaResenias(List.of(resenia));

        AutorEntity autor = new AutorEntity();
        autor.setIdAutor(1);
        autor.setNombre("Julio");
        autor.setApellido("Cortázar");
        autor.setLibros(List.of(libro));

        when(repository.findByIdAutorAndActivoTrue(1)).thenReturn(Optional.of(autor));

        AutorDetalleDTO detalle = service.obtenerDetalleAutor(1);

        assertEquals(1, detalle.libros().size());
        assertEquals("Rayuela", detalle.libros().get(0).getTitulo());
        assertEquals(12500F, detalle.libros().get(0).getPrecio());
        assertEquals("Novela", detalle.libros().get(0).getGeneros().get(0).getNombre());
        assertEquals("Una lectura inolvidable.", detalle.libros().get(0).getResenias().get(0).getTexto());
        assertEquals(new BigDecimal("4.5"),
                detalle.libros().get(0).getResenias().get(0).getCalificacion());
        assertEquals("lectora", detalle.libros().get(0).getResenias().get(0).getNombreUsuario());
    }

    private AutorDTO autorDTO(String nombre, String apellido) {
        AutorDTO dto = new AutorDTO();
        dto.setNombre(nombre);
        dto.setApellido(apellido);
        return dto;
    }
}
