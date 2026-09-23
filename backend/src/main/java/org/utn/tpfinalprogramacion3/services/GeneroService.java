package org.utn.tpfinalprogramacion3.services;

import jakarta.transaction.Transactional;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.Exceptions.GeneroNoEncontrado;
import org.utn.tpfinalprogramacion3.dtos.GeneroDTO;
import org.utn.tpfinalprogramacion3.entities.GeneroEntity;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.repository.GeneroRepository;
import org.utn.tpfinalprogramacion3.repository.LibroRepository;

import java.util.List;
import java.util.Optional;

@Service
public class GeneroService {

    private GeneroRepository generoRepository;
    private LibroRepository libroRepository;
    private ModelMapper modelMapper;

    public GeneroService(GeneroRepository generoRepository, ModelMapper modelMapper, LibroRepository libroRepository) {
        this.generoRepository = generoRepository;
        this.modelMapper = modelMapper;
        this.libroRepository = libroRepository;
    }

    @Transactional
    public GeneroDTO Save(GeneroDTO generoDTO) {
        try {
            GeneroEntity generoEntity = modelMapper.map(generoDTO, GeneroEntity.class);
            generoEntity = generoRepository.save(generoEntity);
            return modelMapper.map(generoEntity, GeneroDTO.class);
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al guardar el genero");
        }
    }
    @PreAuthorize("hasAuthority('VER_GENEROS')")
    public List<GeneroEntity> findAll() {
        return generoRepository.findAll();
    }

    public List<LibroEntity> obtenerLibrosPorNombreGenero(String nombreGenero) {
        GeneroEntity genero = generoRepository.findByNombreIgnoreCase(nombreGenero)
                .orElseThrow(() -> new GeneroNoEncontrado("Genero con nombre: "+nombreGenero+" no encontrado!"));

        return genero.getLibros();
    }


}
