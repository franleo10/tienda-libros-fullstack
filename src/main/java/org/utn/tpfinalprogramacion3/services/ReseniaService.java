package org.utn.tpfinalprogramacion3.services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.dtos.ReseniaDTO;
import org.utn.tpfinalprogramacion3.entities.ReseñaEntity;
import org.utn.tpfinalprogramacion3.repository.ReseniaRepository;
@Service
public class ReseniaService {
    private ReseniaRepository repository;
    private ModelMapper modelMapper;

    public ReseniaService(ReseniaRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }
    public ReseniaDTO crearResenia(ReseniaDTO dto) {
        try {
            ReseñaEntity reseñaEntity=modelMapper.map(dto, ReseñaEntity.class);
            ReseñaEntity reseñaEntity1=repository.save(reseñaEntity);
            return modelMapper.map(reseñaEntity1, ReseniaDTO.class);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Error al crear el resenia");
        }
    }


}
