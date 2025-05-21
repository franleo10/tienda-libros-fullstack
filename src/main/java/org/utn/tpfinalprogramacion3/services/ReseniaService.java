package org.utn.tpfinalprogramacion3.services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.dtos.ReseniaCreateDTO;
import org.utn.tpfinalprogramacion3.dtos.ReseniaDTO;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.entities.ReseniaEntity;
import org.utn.tpfinalprogramacion3.repository.LibroRepository;
import org.utn.tpfinalprogramacion3.repository.ReseniaRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ReseniaService {
    private final LibroRepository libroRepository;
    private final ReseniaRepository reseniaRepository;
    private ReseniaRepository repository;
    private ModelMapper modelMapper;

    public ReseniaService(ReseniaRepository repository, ModelMapper modelMapper, LibroRepository libroRepository, ReseniaRepository reseniaRepository) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.libroRepository = libroRepository;
        this.reseniaRepository = reseniaRepository;
    }

    public ReseniaDTO crearResenia(ReseniaCreateDTO dto) {

        try {

            Optional<LibroEntity> libroBuscado = libroRepository.findById(dto.getLibro().getIdLibro());
            if(libroBuscado.isEmpty()){
                throw new NoSuchElementException("El ID del libro ingresado no existe");
            }

            dto.getLibro().setIdLibro(libroBuscado.get().getIdLibro());
            dto.getLibro().setTitulo(libroBuscado.get().getTitulo());

            ReseniaEntity reseniaEntity=modelMapper.map(dto, ReseniaEntity.class);

            ReseniaEntity reseniaGuardada=repository.save(reseniaEntity);

            return modelMapper.map(reseniaGuardada, ReseniaDTO.class);

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Error al crear el resenia");
        }
    }


}
