package org.utn.tpfinalprogramacion3.services;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.utn.tpfinalprogramacion3.dtos.LibroDTO;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.repository.AutorRepository;
import org.utn.tpfinalprogramacion3.repository.GeneroRepository;
import org.utn.tpfinalprogramacion3.repository.LibroRepository;
import org.utn.tpfinalprogramacion3.repository.ReseniaRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
