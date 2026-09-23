package org.utn.tpfinalprogramacion3.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.Exceptions.BibliotecaNoEncontradaException;
import org.utn.tpfinalprogramacion3.Exceptions.LibroInexistenteException;
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
import org.springframework.security.core.Authentication;
import org.utn.tpfinalprogramacion3.security.entities.CredencialEntity;

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
                .orElseThrow(() -> new UsuarioInexistenteException("Usuario no encontrado"));

        LibroEntity libro = libroRepository.findById(dto.getIdLibro())
                .orElseThrow(() -> new LibroInexistenteException("Libro no encontrado"));

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

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<BibliotecaDTO> findAll() {
        List<BibliotecaDTO> resultado = new ArrayList<>();

        for (UsuarioEntity usuario : usuarioRepository.findAll()) {
            BibliotecaEntity biblioteca = usuario.getBiblioteca();
            if (biblioteca != null && biblioteca.getLibros() != null) {
                List<LibroBibliotecaDTO> librosDTO = biblioteca.getLibros().stream()
                        .map(libro -> toLibroDTO(biblioteca, libro))  // PASO AMBOS PARAMETROS
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
    public BibliotecaDTO getByUsuarioId() {
        // Obtener ID del usuario autenticado desde el token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CredencialEntity credencial = (CredencialEntity) authentication.getPrincipal();
        Long userId = credencial.getId();

        return usuarioRepository.findById(userId.intValue())  // si userId es Long
                .map(usuario -> {
                    BibliotecaEntity biblioteca = usuario.getBiblioteca();

                    if (biblioteca == null) {
                        throw new BibliotecaNoEncontradaException("El usuario no tiene biblioteca");
                    }

                    Set<LibroEntity> libros = Optional.ofNullable(biblioteca.getLibros())
                            .orElse(Collections.emptySet());

                    List<LibroBibliotecaDTO> librosDTO = libros.stream()
                            .map(libro -> toLibroDTO(biblioteca, libro))
                            .collect(Collectors.toList());

                    return BibliotecaDTO.builder()
                            .idUsuario(usuario.getId())
                            .nombreUsuario(usuario.getNombre())
                            .libros(librosDTO)
                            .build();
                })
                .orElseThrow(() -> new UsuarioInexistenteException("Usuario con ID " + userId + " no encontrado"));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public BibliotecaDTO getByUsuarioIdForAdmin(int idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .map(usuario -> {
                    BibliotecaEntity biblioteca = usuario.getBiblioteca();

                    if (biblioteca == null) {
                        throw new BibliotecaNoEncontradaException("El usuario no tiene biblioteca");
                    }

                    Set<LibroEntity> libros = Optional.ofNullable(biblioteca.getLibros())
                            .orElse(Collections.emptySet());

                    List<LibroBibliotecaDTO> librosDTO = libros.stream()
                            .map(libro -> toLibroDTO(biblioteca, libro))  // acá le paso biblioteca y libro
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


    private LibroBibliotecaDTO toLibroDTO(BibliotecaEntity biblioteca,LibroEntity entity) {

        String urlPdf = openLibraryService.obtenerUrlLibroPorTitulo(entity.getTitulo()).block();

        boolean esFavorito = biblioteca.getLibrosFavoritos().contains(entity);

        return LibroBibliotecaDTO.builder()
                .idLibro(entity.getIdLibro())
                .titulo(entity.getTitulo())
                .precio(entity.getPrecio())
                .urlPdf(urlPdf)
                .esFavorito(esFavorito)
                .build();
    }


    @Transactional
    public void marcarLibroFavorito(int idUsuario, int idLibro) {
        BibliotecaEntity biblioteca = bibliotecaRepository.findByUsuarioId(idUsuario)
                .orElseThrow(() -> new BibliotecaNoEncontradaException("Biblioteca no encontrada"));


        Optional<LibroEntity> libroOpt = biblioteca.getLibros().stream()
                .filter(libro -> libro.getIdLibro() == idLibro)
                .findFirst();

        if (libroOpt.isEmpty()) {
            throw new LibroInexistenteException("El libro no está en la biblioteca");
        }

        biblioteca.getLibrosFavoritos().add(libroOpt.get());
        bibliotecaRepository.save(biblioteca);
    }

    @Transactional
    public void desmarcarLibroFavorito(int idUsuario, int idLibro) {
        BibliotecaEntity biblioteca = bibliotecaRepository.findByUsuarioId(idUsuario)
                .orElseThrow(() -> new BibliotecaNoEncontradaException("Biblioteca no encontrada"));
        Optional<LibroEntity> libroOpt = biblioteca.getLibros().stream()
                .filter(libro -> libro.getIdLibro() == idLibro)
                .findFirst();

        if (libroOpt.isEmpty()) {
            throw new LibroInexistenteException("El libro no está en la biblioteca");
        }

        biblioteca.getLibrosFavoritos().removeIf(libro -> libro.getIdLibro() == idLibro);
        bibliotecaRepository.save(biblioteca);
    }

    public List<LibroBibliotecaDTO> getLibrosFavoritos(int idUsuario) {
        BibliotecaEntity biblioteca = bibliotecaRepository.findByUsuarioId(idUsuario)
                .orElseThrow(() -> new RuntimeException("Biblioteca no encontrada"));

        return biblioteca.getLibrosFavoritos().stream()
                .map(libro -> toLibroDTO(biblioteca, libro))
                .collect(Collectors.toList());
    }


}
