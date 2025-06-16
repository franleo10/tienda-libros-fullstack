package org.utn.tpfinalprogramacion3.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.Exceptions.BibliotecaNoEncontradaException;
import org.utn.tpfinalprogramacion3.Exceptions.UsuarioInexistenteException;
import org.utn.tpfinalprogramacion3.dtos.AgregarLibroDTO;
import org.utn.tpfinalprogramacion3.dtos.BibliotecaDTO;
import org.utn.tpfinalprogramacion3.dtos.LibroBibliotecaDTO;
import org.utn.tpfinalprogramacion3.dtos.LibroDTO;
import org.utn.tpfinalprogramacion3.entities.BibliotecaEntity;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.repository.BibliotecaRepository;
import org.utn.tpfinalprogramacion3.repository.LibroRepository;
import org.utn.tpfinalprogramacion3.repository.UsuarioRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BibliotecaService {

    private final BibliotecaRepository bibliotecaRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;
    @Autowired
    private OpenLibraryService openLibraryService;

    public void agregarLibro(AgregarLibroDTO dto) {
        UsuarioEntity usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        LibroEntity libro = libroRepository.findById(dto.getIdLibro())
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        BibliotecaEntity biblioteca = usuario.getBiblioteca();

        if (biblioteca == null) {
            biblioteca = BibliotecaEntity.builder()
                    .usuario(usuario)
                    .libros(new HashSet<>())
                    .build();
            usuario.setBiblioteca(biblioteca);
        } else if (biblioteca.getLibros() == null) {
            biblioteca.setLibros(new HashSet<>());
        }

        biblioteca.getLibros().add(libro);
        bibliotecaRepository.save(biblioteca);
    }

    @PreAuthorize("hasAuthority('VER_BIBLIOTECA')")
    public List<BibliotecaDTO> findAll() {
        List<BibliotecaDTO> resultado = new ArrayList<>();

        for (UsuarioEntity usuario : usuarioRepository.findAll()) {
            BibliotecaEntity biblioteca = usuario.getBiblioteca();
            if (biblioteca != null && biblioteca.getLibros() != null) {
                List<LibroBibliotecaDTO> librosDTO = biblioteca.getLibros().stream()
                        .map(this::toLibroDTO)
                        .collect(Collectors.toList());

                BibliotecaDTO dto = BibliotecaDTO.builder()
                        .idUsuario(usuario.getId())
                        .nombreUsuario(usuario.getNombre())
                        .libros(librosDTO)
                        .build();

                resultado.add(dto);
            }
        }

        return resultado;
    }

    @PreAuthorize("hasAuthority('VER_BIBLIOTECA')")
    public BibliotecaDTO getByUsuarioId(int idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .map(usuario -> {
                    Set<LibroEntity> libros = Optional.ofNullable(usuario.getBiblioteca())
                            .map(BibliotecaEntity::getLibros)
                            .orElse(Collections.emptySet());

                    List<LibroBibliotecaDTO> librosDTO = libros.stream()
                            .map(this::toLibroDTO)
                            .collect(Collectors.toList());

                    return BibliotecaDTO.builder()
                            .idUsuario(usuario.getId())
                            .nombreUsuario(usuario.getNombre())
                            .libros(librosDTO)
                            .build();
                })
                .orElseThrow(() -> new UsuarioInexistenteException("Usuario con ID " + idUsuario + " no encontrado"));
    }



    public void deleteById(int idUsuario) {
        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioInexistenteException("Usuario con ID " + idUsuario + " no encontrado"));


        BibliotecaEntity biblioteca = usuario.getBiblioteca();
        if (biblioteca == null) {
            throw new BibliotecaNoEncontradaException("El usuario no tiene una biblioteca asociada.");
        }

        bibliotecaRepository.delete(biblioteca);
    }



    private LibroBibliotecaDTO toLibroDTO(LibroEntity entity) {

        String urlPdf = openLibraryService.obtenerUrlLibroPorTitulo(entity.getTitulo()).block();

        return LibroBibliotecaDTO.builder()
                .idLibro(entity.getIdLibro())
                .titulo(entity.getTitulo())
                .precio(entity.getPrecio())
                .urlPdf(urlPdf)
                .build();
    }
}
