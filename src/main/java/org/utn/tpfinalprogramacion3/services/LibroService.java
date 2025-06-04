package org.utn.tpfinalprogramacion3.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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


    public List<LibroEntity> getAllLibros() {
        return libroRepository.findAll();
    }


    public ResponseEntity<?> agregarGeneroALibro(int idLibro, int idGenero) {
        LibroEntity libro = libroRepository.findById(idLibro)
                .orElseThrow(()-> new EntityNotFoundException("Libro no encontrado con id: " + idLibro));


        GeneroEntity genero=generoRepository.findById(idGenero).orElseThrow(()->new EntityNotFoundException("Genero no encontrado con id: " + idGenero));

        if (!libro.getGeneros().contains(genero)) {
            libro.getGeneros().add(genero);
            genero.getLibros().add(libro);
            libroRepository.save(libro);
            generoRepository.save(genero);
        } else {
            throw  new IllegalArgumentException("La genero ya existe en el libro");
        }

        return ResponseEntity.ok("Género agregado correctamente al libro.");
    }

    public ResponseEntity<?> agregarAutorALibro(int idLibro, int idAutor) {
        LibroEntity libro = libroRepository.findById(idLibro)
                .orElseThrow(()-> new EntityNotFoundException("Libro no encontrado con id: " + idLibro));


        AutorEntity autor=autorRepository.findById(idAutor)
                .orElseThrow(()->new EntityNotFoundException("Autor no encontrado con id: " + idAutor));

        if (!libro.getAutores().contains(autor)) {
            libro.getAutores().add(autor);
            autor.getLibros().add(libro);
            libroRepository.save(libro);
            autorRepository.save(autor);
        } else {
            throw new IllegalArgumentException("Autor ya existe en el libro");
        }
        return ResponseEntity.ok("Autor agregado correctamente al libro.");
    }

    public ResponseEntity<?> eliminarAutorALibro(int idLibro, int idAutor) {

        LibroEntity libro = libroRepository.findById(idLibro)
                .orElseThrow(()-> new EntityNotFoundException("Libro no encontrado con id: " + idLibro));


        AutorEntity autor=autorRepository.findById(idAutor)
                .orElseThrow(()->new EntityNotFoundException("Autor no encontrado con id: " + idAutor));


        if (libro.getAutores().contains(autor)) {
           libro.getAutores().remove(autor);
           autor.getLibros().remove(libro);
            libroRepository.save(libro);
            autorRepository.save(autor);
        } else {
            throw new IllegalArgumentException("Autor no existe en el libro");
        }
        return ResponseEntity.ok("Autor eliminado correctamente del libro.");

    }
}