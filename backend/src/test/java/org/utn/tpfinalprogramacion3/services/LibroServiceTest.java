package org.utn.tpfinalprogramacion3.services;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.utn.tpfinalprogramacion3.dtos.LibroDTO;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.repository.AutorRepository;
import org.utn.tpfinalprogramacion3.repository.GeneroRepository;
import org.utn.tpfinalprogramacion3.repository.LibroRepository;
import org.utn.tpfinalprogramacion3.repository.ReseniaRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LibroServiceTest {

    @Test
    void listarLibrosPaginaSoloLosActivosYCuentaSoloEsos() {
        LibroRepository repository = mock(LibroRepository.class);
        LibroService service = servicio(repository);
        Pageable primeraPagina = PageRequest.of(0, 5);
        // La base devuelve 5 libros activos de un total de 8 activos (los dados de baja no se cuentan)
        List<LibroEntity> activos = List.of(libro(1), libro(2), libro(3), libro(4), libro(6));

        when(repository.findAllByActivoTrue(primeraPagina))
                .thenReturn(new PageImpl<>(activos, primeraPagina, 8));

        Page<LibroDTO> pagina = service.getAllLibros(0);

        assertEquals(5, pagina.getContent().size());
        assertEquals(8, pagina.getTotalElements());
        assertEquals(2, pagina.getTotalPages());
        verify(repository, never()).findAll(any(Pageable.class));
    }

    @Test
    void listarLibrosInactivosPaginaSoloLosDadosDeBaja() {
        LibroRepository repository = mock(LibroRepository.class);
        LibroService service = servicio(repository);
        Pageable primeraPagina = PageRequest.of(0, 5);
        LibroEntity dadoDeBaja = libro(5);
        dadoDeBaja.setActivo(false);

        when(repository.findAllByActivoFalse(primeraPagina))
                .thenReturn(new PageImpl<>(List.of(dadoDeBaja), primeraPagina, 1));

        Page<LibroDTO> pagina = service.getAllLibrosInactivos(0);

        assertEquals(1, pagina.getContent().size());
        assertEquals(1, pagina.getTotalElements());
        verify(repository, never()).findAll(any(Pageable.class));
    }

    @Test
    void modificarLibroConUrlPortadaActualizaLaPortada() {
        LibroRepository repository = repositorioConLibro(libro(7));
        LibroService service = servicio(repository);

        LibroDTO modificado = service.modificarLibro(7, datosModificados("  https://ejemplo.com/tapa.jpg "));

        assertEquals("https://ejemplo.com/tapa.jpg", modificado.getUrlPortada());
    }

    @Test
    void modificarLibroSinUrlPortadaConservaLaPortadaActual() {
        LibroEntity existente = libro(7);
        existente.setUrlPortada("https://ejemplo.com/vieja.jpg");
        LibroService service = servicio(repositorioConLibro(existente));

        LibroDTO modificado = service.modificarLibro(7, datosModificados(null));

        assertEquals("https://ejemplo.com/vieja.jpg", modificado.getUrlPortada());
    }

    @Test
    void modificarLibroConUrlPortadaVaciaQuitaLaPortada() {
        LibroEntity existente = libro(7);
        existente.setUrlPortada("https://ejemplo.com/vieja.jpg");
        LibroService service = servicio(repositorioConLibro(existente));

        LibroDTO modificado = service.modificarLibro(7, datosModificados("   "));

        assertNull(modificado.getUrlPortada());
    }

    @Test
    void buscarLibrosArmaElPatronEnMinusculasYSinEspacios() {
        LibroRepository repository = mock(LibroRepository.class);
        LibroService service = servicio(repository);
        Pageable primeraPagina = PageRequest.of(0, 5, Sort.by("idLibro"));
        when(repository.buscarActivos(eq("%borges%"), anyInt(), anyFloat(), anyFloat(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(libro(1)), primeraPagina, 1));

        Page<LibroDTO> pagina = service.buscarLibros("  BORGes ", null, null, null, null, 0);

        assertEquals(1, pagina.getTotalElements());
    }

    @Test
    void buscarLibrosSinFiltrosNoRestringeGeneroNiPrecio() {
        LibroRepository repository = mock(LibroRepository.class);
        LibroService service = servicio(repository);
        Pageable primeraPagina = PageRequest.of(0, 5);
        when(repository.buscarActivos(any(), anyInt(), anyFloat(), anyFloat(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), primeraPagina, 0));

        Page<LibroDTO> pagina = service.buscarLibros(null, null, null, null, null, 0);

        // Página vacía en vez de un error, y los valores por defecto no filtran nada
        assertEquals(0, pagina.getTotalElements());
        verify(repository).buscarActivos(eq("%%"), eq(0), eq(0f), eq(999_999_999f), any(Pageable.class));
    }

    @Test
    void buscarLibrosPasaGeneroYRangoDePrecio() {
        LibroRepository repository = mock(LibroRepository.class);
        LibroService service = servicio(repository);
        when(repository.buscarActivos(any(), anyInt(), anyFloat(), anyFloat(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        service.buscarLibros("", 3, 100f, 500f, null, 0);

        verify(repository).buscarActivos(eq("%%"), eq(3), eq(100f), eq(500f), any(Pageable.class));
    }

    @Test
    void buscarLibrosOrdenaSoloPorLosCriteriosConocidos() {
        LibroRepository repository = mock(LibroRepository.class);
        LibroService service = servicio(repository);
        when(repository.buscarActivos(any(), anyInt(), anyFloat(), anyFloat(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);

        service.buscarLibros("", null, null, null, "precio_desc", 0);
        service.buscarLibros("", null, null, null, "; DROP TABLE libros", 0);

        verify(repository, times(2)).buscarActivos(any(), anyInt(), anyFloat(), anyFloat(), pageable.capture());
        // El criterio pedido va primero y el id desempata
        assertEquals("precio: DESC,idLibro: ASC", pageable.getAllValues().get(0).getSort().toString());
        // Un valor desconocido se ignora y queda el orden por defecto
        assertEquals("idLibro: ASC", pageable.getAllValues().get(1).getSort().toString());
    }

    private LibroRepository repositorioConLibro(LibroEntity libro) {
        LibroRepository repository = mock(LibroRepository.class);
        when(repository.findById(libro.getIdLibro())).thenReturn(Optional.of(libro));
        when(repository.save(any(LibroEntity.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        return repository;
    }

    private LibroDTO datosModificados(String urlPortada) {
        LibroDTO dto = new LibroDTO();
        dto.setTitulo("Titulo nuevo");
        dto.setSinopsis("Sinopsis nueva");
        dto.setPrecio(20f);
        dto.setUrlPortada(urlPortada);
        return dto;
    }

    private LibroService servicio(LibroRepository repository) {
        return new LibroService(
                repository,
                mock(ReseniaRepository.class),
                new ModelMapper(),
                mock(AutorRepository.class),
                mock(GeneroRepository.class),
                mock(OpenLibraryService.class));
    }

    private LibroEntity libro(int id) {
        LibroEntity libro = new LibroEntity();
        libro.setIdLibro(id);
        libro.setTitulo("Libro " + id);
        libro.setPrecio(10f);
        libro.setActivo(true);
        return libro;
    }
}
