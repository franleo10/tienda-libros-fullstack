package org.utn.tpfinalprogramacion3.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.Exceptions.AutorNoEncontrado;
import org.utn.tpfinalprogramacion3.dtos.AutorDTO;
import org.utn.tpfinalprogramacion3.entities.AutorEntity;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.repository.AutorRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AutorService {

    private final ModelMapper modelMapper;
    private final AutorRepository autorRepository;

    @Autowired
    public AutorService(AutorRepository autorRepository, ModelMapper modelMapper) {
        this.autorRepository = autorRepository;
        this.modelMapper = modelMapper;
    }

    public ResponseEntity<AutorDTO> createAutor(AutorDTO autorDTO) {
        if (autorRepository.findByNombreAndApellido(autorDTO.getNombre(), autorDTO.getApellido()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        AutorEntity autorEntity = modelMapper.map(autorDTO, AutorEntity.class);
        autorEntity = autorRepository.save(autorEntity);
        AutorDTO resultado = modelMapper.map(autorEntity, AutorDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    public ResponseEntity<List<AutorEntity>> listarAutores() {
        List<AutorEntity> autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(autores);
    }
    public ResponseEntity<String> borrarAutor(int id) {
        AutorEntity autorOptional = autorRepository.findById(id).orElseThrow(() -> new AutorNoEncontrado("Autor " + id + " no existe"));

        autorRepository.deleteById(id);
        return ResponseEntity.ok("Autor eliminado.");
    }
    public ResponseEntity<List<AutorEntity>> listarPorNombre(String nombre) {
        List<AutorEntity> autores = autorRepository.findAllByNombre(nombre);
        if (autores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(autores);
    }

    public List<LibroEntity>obtenerLibrosPorNombreAutor(String nombre) {
        AutorEntity autorEntity=autorRepository.findByNombreIgnoreCase(nombre).orElseThrow(()->new AutorNoEncontrado("Autor: "+nombre+" no existe"));
        return autorEntity.getLibros();
    }

    public AutorEntity actualizarAutor(Integer id, AutorEntity nuevoAutor) {
        AutorEntity autorExistente = autorRepository.findById(id)
                .orElseThrow(() -> new AutorNoEncontrado("Autor con ID " + id + " no encontrado"));

        autorExistente.setNombre(nuevoAutor.getNombre());
        autorExistente.setApellido(nuevoAutor.getApellido());

        return autorRepository.save(autorExistente);
    }




}
