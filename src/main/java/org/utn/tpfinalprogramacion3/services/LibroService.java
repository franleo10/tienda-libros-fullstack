package org.utn.tpfinalprogramacion3.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.utn.tpfinalprogramacion3.dtos.LibroDTO;
import org.utn.tpfinalprogramacion3.entities.AutorEntity;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.mapper.ModelMapperConfig;
import org.utn.tpfinalprogramacion3.repository.AutorRepository;
import org.utn.tpfinalprogramacion3.repository.LibroRepository;

import java.util.Optional;

@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final ModelMapper modelMapper;
    @Autowired
    public LibroService(LibroRepository libroRepository, ModelMapper modelMapper, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.modelMapper = modelMapper;
        this.autorRepository = autorRepository;
    }

    public Optional<LibroDTO> crearLibro(LibroDTO libroDTO, int autor_id){
        try{
            LibroEntity libro=modelMapper.map(libroDTO, LibroEntity.class);
            if (!autorRepository.findByidAutor(autor_id).isPresent()){
                return Optional.empty();
            }
            libro.getAutores().add(autorRepository.findByidAutor(autor_id).get());
            libro=libroRepository.save(libro);
            return Optional.of(modelMapper.map(libro, LibroDTO.class));
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Error al crear el libro");
        }
    }

}
