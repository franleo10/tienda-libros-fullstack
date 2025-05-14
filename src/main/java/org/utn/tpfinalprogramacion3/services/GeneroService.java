package org.utn.tpfinalprogramacion3.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.utn.tpfinalprogramacion3.dtos.GeneroDTO;
import org.utn.tpfinalprogramacion3.entities.GeneroEntity;
import org.utn.tpfinalprogramacion3.repository.GeneroRepository;

public class GeneroService {

    private GeneroRepository generoRepository;
    private ModelMapper modelMapper;

    public GeneroService(GeneroRepository generoRepository, ModelMapper modelMapper) {
        this.generoRepository = generoRepository;
        this.modelMapper = modelMapper;
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
}
