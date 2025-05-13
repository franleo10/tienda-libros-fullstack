package org.utn.tpfinalprogramacion3.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.dtos.AutorDTO;
import org.utn.tpfinalprogramacion3.entities.AutorEntity;
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

    public Optional<AutorDTO> createAutor(AutorDTO autorDTO) {

        AutorEntity autorEntity = modelMapper.map(autorDTO, AutorEntity.class);

        if(autorRepository.findByNombreAndApellido(autorDTO.getNombre(), autorDTO.getApellido()).isPresent()){
            return Optional.empty();
        }

        autorRepository.save(autorEntity);

        return Optional.of(modelMapper.map(autorEntity, AutorDTO.class));
    }

}
