package org.utn.tpfinalprogramacion3.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    public Optional<BibliotecaDTO> findById(int idUsuario) {
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
                });
    }


    public void deleteById(int id) {
        if (!bibliotecaRepository.existsById(id)) {
            throw new RuntimeException("La biblioteca con ID " + id + " no existe.");
        }
        bibliotecaRepository.deleteById(id);
    }


    private LibroBibliotecaDTO toLibroDTO(LibroEntity entity) {
        return LibroBibliotecaDTO.builder()
                .idLibro(entity.getIdLibro())
                .titulo(entity.getTitulo())
                .precio(entity.getPrecio())
                .build();
    }
}
