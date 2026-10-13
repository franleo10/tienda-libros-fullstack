package org.utn.tpfinalprogramacion3.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.Exceptions.AutorExistenteException;
import org.utn.tpfinalprogramacion3.Exceptions.AutorNoEncontrado;
import org.utn.tpfinalprogramacion3.Exceptions.NoHayLibrosException;
import org.utn.tpfinalprogramacion3.dtos.AutorDTO;
import org.utn.tpfinalprogramacion3.dtos.AutorDetalleDTO;
import org.utn.tpfinalprogramacion3.dtos.AutorResumenDTO;
import org.utn.tpfinalprogramacion3.dtos.GeneroDTO;
import org.utn.tpfinalprogramacion3.dtos.LibroDTO;
import org.utn.tpfinalprogramacion3.dtos.ReseniaDTOLibro;
import org.utn.tpfinalprogramacion3.entities.AutorEntity;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.repository.AutorRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AutorService {

    private final ModelMapper modelMapper;
    private final AutorRepository autorRepository;

    @Autowired
    public AutorService(AutorRepository autorRepository, ModelMapper modelMapper) {
        this.autorRepository = autorRepository;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("hasAuthority('AGREGAR_AUTOR')")
    public String createAutor(AutorDTO autorDTO) {
        String nombre = autorDTO.getNombre().trim();
        String apellido = autorDTO.getApellido().trim();

        if (autorRepository.existsByNombreIgnoreCaseAndApellidoIgnoreCase(nombre, apellido)) {
            throw new AutorExistenteException("Ya existe un autor con ese nombre y apellido.");
        }

        autorRepository.save(new AutorEntity(nombre, apellido));
        return "Autor agregado con éxito.";
    }

    @PreAuthorize("hasAuthority('VER_AUTORES')")
    @Transactional
    public Page<AutorResumenDTO> listarAutores(int numeroPagina) {
        Pageable pageable = PageRequest.of(numeroPagina, 5);
        return mapearResumen(autorRepository.findAllByActivoTrue(pageable), pageable);
    }

    @PreAuthorize("hasAuthority('ELIMINAR_AUTOR')")
    @Transactional
    public Page<AutorResumenDTO> listarAutoresInactivos(int numeroPagina) {
        Pageable pageable = PageRequest.of(numeroPagina, 5);
        return mapearResumen(autorRepository.findAllByActivoFalse(pageable), pageable);
    }

    @PreAuthorize("hasAuthority('VER_AUTORES')")
    @Transactional
    public Page<AutorResumenDTO> buscarAutores(String termino, int numeroPagina) {
        Pageable pageable = PageRequest.of(numeroPagina, 5);
        return mapearResumen(autorRepository.buscarPorNombre(termino.trim(), pageable), pageable);
    }

    @PreAuthorize("hasAuthority('ELIMINAR_AUTOR')")
    @Transactional
    public Page<AutorResumenDTO> buscarAutoresInactivos(String termino, int numeroPagina) {
        Pageable pageable = PageRequest.of(numeroPagina, 5);
        return mapearResumen(autorRepository.buscarInactivosPorNombre(termino.trim(), pageable), pageable);
    }

    private Page<AutorResumenDTO> mapearResumen(Page<AutorEntity> paginaAutores, Pageable pageable) {
        List<Integer> idsAutores = paginaAutores.getContent().stream()
                .map(AutorEntity::getIdAutor)
                .toList();
        Map<Integer, Long> cantidadLibrosPorAutor = idsAutores.isEmpty()
                ? Map.of()
                : autorRepository.contarLibrosPorAutor(idsAutores).stream()
                        .collect(Collectors.toMap(
                                fila -> (Integer) fila[0],
                                fila -> (Long) fila[1]
                        ));

        List<AutorResumenDTO> listaAutorDTO = paginaAutores.getContent().stream()
                .map(autor -> new AutorResumenDTO(
                        autor.getIdAutor(),
                        autor.getNombre(),
                        autor.getApellido(),
                        cantidadLibrosPorAutor.getOrDefault(autor.getIdAutor(), 0L)
                ))
                .toList();

        return new PageImpl<>(listaAutorDTO, pageable, paginaAutores.getTotalElements());
    }

    @PreAuthorize("hasAuthority('VER_AUTORES')")
    @Transactional
    public AutorDetalleDTO obtenerDetalleAutor(int idAutor) {
        AutorEntity autor = autorRepository.findByIdAutorAndActivoTrue(idAutor)
                .orElseThrow(() -> new AutorNoEncontrado(
                        "No existe un autor con el ID indicado en el sistema."));

        List<LibroDTO> libros = autor.getLibros().stream()
                .map(this::mapearLibroDetalle)
                .toList();

        return new AutorDetalleDTO(
                autor.getIdAutor(),
                autor.getNombre(),
                autor.getApellido(),
                libros
        );
    }

    private LibroDTO mapearLibroDetalle(LibroEntity libro) {
        LibroDTO libroDTO = modelMapper.map(libro, LibroDTO.class);
        libroDTO.setId(libro.getIdLibro());
        libroDTO.setGeneros(libro.getGeneros().stream()
                .map(genero -> {
                    GeneroDTO generoDTO = new GeneroDTO();
                    generoDTO.setNombre(genero.getNombre());
                    return generoDTO;
                })
                .toList());
        libroDTO.setResenias(libro.getListaResenias().stream()
                .map(resenia -> ReseniaDTOLibro.builder()
                        .id((long) resenia.getIdResenia())
                        .texto(resenia.getTexto())
                        .calificacion(resenia.getCalificacion())
                        .nombreUsuario(resenia.getUsuario().getNombre())
                        .build())
                .toList());
        return libroDTO;
    }

    @PreAuthorize("hasAuthority('ELIMINAR_AUTOR')")
    @Transactional
    public String darDeBajaAutor(int id) {
        AutorEntity autor = autorRepository.findById(id)
                .orElseThrow(() -> new AutorNoEncontrado(
                        "No existe un autor con el ID indicado en el sistema."));

        autor.setActivo(false);
        autorRepository.save(autor);

        return "Autor dado de baja correctamente.";
    }

    @PreAuthorize("hasAuthority('ELIMINAR_AUTOR')")
    @Transactional
    public String darDeAltaAutor(int id) {
        AutorEntity autor = autorRepository.findById(id)
                .orElseThrow(() -> new AutorNoEncontrado(
                        "No existe un autor con el ID indicado en el sistema."));

        autor.setActivo(true);
        autorRepository.save(autor);

        return "Autor reactivado correctamente.";
    }

    @PreAuthorize("hasAuthority('VER_AUTORES')")
    public Page<AutorDTO>  listarPorNombre(String nombre, int numeroPagina) {

        int tamañoPagina = 5;  // o el tamaño que quieras fijo
        Pageable pageable = PageRequest.of(numeroPagina, tamañoPagina);

        Page<AutorEntity> autores = autorRepository.findAllByNombreIgnoreCaseContainingAndActivoTrue(nombre, pageable);

        if (autores.isEmpty()) {
            throw new AutorNoEncontrado("No hay autores en el sistema.");
        }

        List<AutorDTO> listaAutorDTO = autores.getContent().stream()
                                        .map(i -> modelMapper.map(i, AutorDTO.class))
                                        .toList();

        return new PageImpl<>(listaAutorDTO, pageable, autores.getTotalElements());
    }

    @PreAuthorize("hasAuthority('VER_AUTORES')")
    public Page<LibroDTO>obtenerLibrosPorNombreAutor(String nombre, int numeroPagina) {

        if(autorRepository.findByNombreIgnoreCaseAndActivoTrue(nombre).isEmpty()){
            throw new AutorNoEncontrado("No existe un autor con ese nombre.");
        }

        AutorEntity autor = autorRepository.findByNombreIgnoreCaseAndActivoTrue(nombre).get();

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

    @PreAuthorize("hasAuthority('MODIFICAR_AUTOR')")
    public String actualizarAutor(Integer id, AutorDTO nuevoAutor) {
        AutorEntity autorExistente = autorRepository.findByIdAutorAndActivoTrue(id)
                .orElseThrow(() -> new AutorNoEncontrado("No se encontró el autor que querés editar."));

        String nombre = nuevoAutor.getNombre().trim();
        String apellido = nuevoAutor.getApellido().trim();
        if (autorRepository.existsByNombreIgnoreCaseAndApellidoIgnoreCaseAndIdAutorNot(
                nombre,
                apellido,
                id
        )) {
            throw new AutorExistenteException("Ya existe un autor con ese nombre y apellido.");
        }

        autorExistente.setNombre(nombre);
        autorExistente.setApellido(apellido);

        autorRepository.save(autorExistente);

        return "Autor actualizado con éxito.";
    }




}
