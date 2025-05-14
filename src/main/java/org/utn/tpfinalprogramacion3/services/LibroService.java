package org.utn.tpfinalprogramacion3.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.utn.tpfinalprogramacion3.dtos.LibroDTO;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.mapper.ModelMapperConfig;
import org.utn.tpfinalprogramacion3.repository.LibroRepository;

public class LibroService {

    private final LibroRepository libroRepository;
    private final ModelMapperConfig modelMapper;
    @Autowired
    public LibroService(LibroRepository libroRepository, ModelMapperConfig modelMapper) {
        this.libroRepository = libroRepository;
        this.modelMapper = modelMapper;
    }

    public Boolean crearAutor(LibroDTO libroDTO){

        LibroEntity libro =

    }

}
