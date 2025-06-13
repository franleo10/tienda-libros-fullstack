package org.utn.tpfinalprogramacion3.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.Exceptions.AutorExistenteException;
import org.utn.tpfinalprogramacion3.Exceptions.AutorNoEncontrado;
import org.utn.tpfinalprogramacion3.Exceptions.NoHayLibrosException;
import org.utn.tpfinalprogramacion3.dtos.AutorDTO;
import org.utn.tpfinalprogramacion3.dtos.LibroDTO;
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

    public String createAutor(AutorDTO autorDTO) {

        if (autorRepository.findByNombreAndApellido(autorDTO.getNombre(), autorDTO.getApellido()).isPresent()) {
            throw new AutorExistenteException("El autor que intenta agregar ya existe en el sistema.");
        }

        AutorEntity autorEntity = modelMapper.map(autorDTO, AutorEntity.class);
        autorEntity = autorRepository.save(autorEntity);
        
        return "Autor agregado con exito.";
    }

    public Page<AutorDTO> listarAutores(int numeroPagina) {

        int tamañoPagina = 5;  // o el tamaño que quieras fijo
        Pageable pageable = PageRequest.of(numeroPagina, tamañoPagina);

        Page<AutorEntity> paginaAutores = autorRepository.findAll(pageable);
        
        if(paginaAutores.isEmpty()){
            throw new NoHayLibrosException("No existen libros en el sistema.");
        }

        List<AutorDTO> listaAutorDTO = paginaAutores.getContent()
        .stream()
        .map(i -> modelMapper.map(i, AutorDTO.class))
        .toList();

        return new PageImpl<>(listaAutorDTO, pageable, paginaAutores.getTotalElements());
    }

    public String borrarAutor(int id) {
        
        if(autorRepository.findById(id).isEmpty()){
            throw new AutorNoEncontrado("No existe un autor con el ID indicado en el sistema.");
        }

        autorRepository.deleteById(id);

        return "Autor eliminado correctamente.";
    }

    public Page<AutorDTO>  listarPorNombre(String nombre, int numeroPagina) {

        int tamañoPagina = 5;  // o el tamaño que quieras fijo
        Pageable pageable = PageRequest.of(numeroPagina, tamañoPagina);

        Page<AutorEntity> autores = autorRepository.findAllByNombre(nombre, pageable);

        if (autores.isEmpty()) {
            throw new AutorNoEncontrado("No hay autores en el sistema.");
        }

        List<AutorDTO> listaAutorDTO = autores.getContent().stream()
                                        .map(i -> modelMapper.map(i, AutorDTO.class))
                                        .toList();

        return new PageImpl<>(listaAutorDTO, pageable, autores.getTotalElements());
    }

    public Page<LibroDTO>obtenerLibrosPorNombreAutor(String nombre, int numeroPagina) {

        if(autorRepository.findByNombreIgnoreCase(nombre).isEmpty()){
            throw new AutorNoEncontrado("No existe un autor con ese nombre.");
        }

        AutorEntity autor = autorRepository.findByNombreIgnoreCase(nombre).get();

        List<LibroEntity> listaLibros = autor.getLibros();

        if(listaLibros.isEmpty()){
            throw new NoHayLibrosException("El autor no tiene libros asignados.");
        }

        int tamañoPagina = 5;  // o el tamaño que quieras fijo
        Pageable pageable = PageRequest.of(numeroPagina, tamañoPagina);

        List<LibroDTO> listaLibroDTO = listaLibros.stream()
        .map(i -> modelMapper.map(i, LibroDTO.class))
        .toList();


        int total = listaLibroDTO.size();
        int desde = (int) pageable.getOffset();
        int hasta = Math.min((desde + pageable.getPageSize()), total);

        List<LibroDTO> subLista = listaLibroDTO.subList(desde, hasta);

        return new PageImpl<>(subLista, pageable, total);
    }

    public String actualizarAutor(Integer id, AutorEntity nuevoAutor) {
        AutorEntity autorExistente = autorRepository.findById(id)
                .orElseThrow(() -> new AutorNoEncontrado("Autor con ID " + id + " no encontrado"));

        if(autorRepository.findById(id).isEmpty()){
            throw new AutorNoEncontrado("No se encontro un autor con el id ingresado.");
        }

        autorExistente.setNombre(nuevoAutor.getNombre());
        autorExistente.setApellido(nuevoAutor.getApellido());

        autorRepository.save(autorExistente);

        return "Autor actualizado con exito";
    }




}
