package org.utn.tpfinalprogramacion3.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.utn.tpfinalprogramacion3.dtos.LibroDTO;
import org.utn.tpfinalprogramacion3.entities.AutorEntity;
import org.utn.tpfinalprogramacion3.entities.GeneroEntity;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.mapper.ModelMapperConfig;
import org.utn.tpfinalprogramacion3.repository.AutorRepository;
import org.utn.tpfinalprogramacion3.repository.GeneroRepository;
import org.utn.tpfinalprogramacion3.repository.LibroRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final GeneroRepository generoRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public LibroService(LibroRepository libroRepository, ModelMapper modelMapper, AutorRepository autorRepository, GeneroRepository generoRepository) {
        this.libroRepository = libroRepository;
        this.modelMapper = modelMapper;
        this.autorRepository = autorRepository;
        this.generoRepository = generoRepository;
    }

    @Transactional
    public Optional<LibroDTO> crearLibro(LibroDTO libroDTO, int autor_id, int genero_id) {
        try {
            LibroEntity libro = modelMapper.map(libroDTO, LibroEntity.class);

            Optional<AutorEntity> autorOpt = autorRepository.findByidAutor(autor_id);
            if (autorOpt.isEmpty()) {
                return Optional.empty();
            }

            Optional<GeneroEntity> generoOpt = generoRepository.findById(genero_id);
            if (generoOpt.isEmpty()) {
                return Optional.empty();
            }

            AutorEntity autor = autorOpt.get();
            GeneroEntity genero = generoOpt.get();

            libro.getAutores().add(autor);
            libro.getGeneros().add(genero);


            libro = libroRepository.save(libro);


            return Optional.of(modelMapper.map(libro, LibroDTO.class));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear el libro");
        }
    }


    public Page<LibroDTO> getAllLibros(int numeroPagina) {
        int tamañoPagina = 5;  // o el tamaño que quieras fijo
        Pageable pageable = PageRequest.of(numeroPagina, tamañoPagina);

        Page<LibroEntity> paginaLibros = libroRepository.findAll(pageable);

        if (paginaLibros.isEmpty()) {
            throw new RuntimeException("No hay libros disponibles."); // O la excepción que quieras
        }

        List<LibroDTO> listaLibrosDTO = paginaLibros.getContent().stream()
                .map(libro -> modelMapper.map(libro, LibroDTO.class))
                .toList();

        return new PageImpl<>(listaLibrosDTO, pageable, paginaLibros.getTotalElements());
    }


    public String agregarGeneroALibro(int idLibro, int idGenero) {
        LibroEntity libro = libroRepository.findById(idLibro)
                .orElseThrow(() -> new EntityNotFoundException("Libro no encontrado con id: " + idLibro));

        GeneroEntity genero = generoRepository.findById(idGenero)
                .orElseThrow(() -> new EntityNotFoundException("Género no encontrado con id: " + idGenero));

        if (!libro.getGeneros().contains(genero)) {
            libro.getGeneros().add(genero);
            genero.getLibros().add(libro);
            libroRepository.save(libro);
            generoRepository.save(genero);
            return "Género agregado correctamente al libro.";
        } else {
            throw new IllegalArgumentException("El género ya existe en el libro");
        }
    }

    public String agregarAutorALibro(int idLibro, int idAutor) {
        LibroEntity libro = libroRepository.findById(idLibro)
                .orElseThrow(() -> new EntityNotFoundException("Libro no encontrado con id: " + idLibro));

        AutorEntity autor = autorRepository.findById(idAutor)
                .orElseThrow(() -> new EntityNotFoundException("Autor no encontrado con id: " + idAutor));

        if (!libro.getAutores().contains(autor)) {
            libro.getAutores().add(autor);
            autor.getLibros().add(libro);
            libroRepository.save(libro);
            autorRepository.save(autor);
            return "Autor agregado correctamente al libro.";
        } else {
            throw new IllegalArgumentException("El autor ya existe en el libro");
        }
    }
    public String eliminarAutorALibro(int idLibro, int idAutor) {
        LibroEntity libro = libroRepository.findById(idLibro)
                .orElseThrow(() -> new EntityNotFoundException("Libro no encontrado con id: " + idLibro));

        AutorEntity autor = autorRepository.findById(idAutor)
                .orElseThrow(() -> new EntityNotFoundException("Autor no encontrado con id: " + idAutor));

        if (libro.getAutores().contains(autor)) {
            libro.getAutores().remove(autor);
            autor.getLibros().remove(libro);
            libroRepository.save(libro);
            autorRepository.save(autor);
            return "Autor eliminado correctamente del libro.";
        } else {
            throw new IllegalArgumentException("Autor no existe en el libro");
        }
    }
}