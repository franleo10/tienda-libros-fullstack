package org.utn.tpfinalprogramacion3.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.Exceptions.CarritoInexistente;
import org.utn.tpfinalprogramacion3.Exceptions.LibroInexistenteException;
import org.utn.tpfinalprogramacion3.Exceptions.UsuarioInexistenteException;
import org.utn.tpfinalprogramacion3.dtos.*;
import org.utn.tpfinalprogramacion3.entities.CarritoEntity;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.repository.CarritoRepository;
import org.utn.tpfinalprogramacion3.repository.LibroRepository;
import org.utn.tpfinalprogramacion3.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.utn.tpfinalprogramacion3.security.entities.CredencialEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CarritoService(CarritoRepository carritoRepository, LibroRepository libroRepository, UsuarioRepository usuarioRepository, UsuarioService usuarioService, ModelMapper modelMapper) {
        this.carritoRepository = carritoRepository;
        this.libroRepository = libroRepository;
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
    }
    @PreAuthorize("hasAuthority('VER_CARRITO')")
    public Optional<CarritoDTO> createCarrito(CarritoDTO carritoDTO, Integer idUsuario) {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Optional<CarritoEntity> carritoExistente = carritoRepository.findByUsuarioId(usuarioEntity.getId());
        if (carritoExistente.isPresent()) {
            return carritoExistente.map(this::mapCarritoToDto);
        }

        CarritoEntity nuevoCarrito = new CarritoEntity();
        nuevoCarrito.setUsuario(usuarioEntity);
        nuevoCarrito.setLibros(List.of()); // carrito vacío

        CarritoEntity guardado = carritoRepository.save(nuevoCarrito);

        return Optional.of(mapCarritoToDto(guardado));
    }

    @PreAuthorize("hasAuthority('VER_TODOS_LOS_CARRITOS')")
    public List<CarritoDTO> listarTodos() {
        return carritoRepository.findAll()
                .stream()
                .map(this::mapCarritoToDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('VER_CARRITO')")
    public Optional<CarritoEntity> buscarPorId(Integer id) {

        return carritoRepository.findById(id);
    }

    @PreAuthorize("hasAuthority('VER_CARRITO')")
    public Optional<CarritoEntity> buscarPorUsuario(Authentication authentication) {
        String email = authentication.getName(); // extrae email del token
        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioInexistenteException("Usuario no encontrado con email: " + email));

        return carritoRepository.findByUsuarioId(usuario.getCarrito().getIdCarrito());
    }


    @PreAuthorize("hasAuthority('ELIMINAR_USUARIOS')")
    public void eliminar(Integer id) {
        CarritoEntity carrito = carritoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        // eliminar libros
        carrito.getLibros().clear();

        // elimina usuario
        UsuarioEntity usuario = carrito.getUsuario();
        if (usuario != null) {
            usuario.setCarrito(null);
        }

        carritoRepository.save(carrito);

        // eliminar carrito
        carritoRepository.deleteById(id);
    }

    @PreAuthorize("hasAuthority('AGREGAR_LIBRO_AL_CARRITO')")
    public String agregarLibro(Integer idCarrito, Integer idLibro) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CredencialEntity credencial = (CredencialEntity) authentication.getPrincipal();
        Long userId = credencial.getId();

        CarritoEntity carrito = carritoRepository.findById(idCarrito)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        // Verificar que el carrito pertenece al usuario
        if (carrito.getUsuario().getId()!=(userId)) {
            throw new CarritoInexistente("No tienes permiso para modificar este carrito");
        }

        LibroEntity libro = libroRepository.findById(idLibro)
                .orElseThrow(() -> new LibroInexistenteException("Libro no encontrado: " + idLibro));

        if (carrito.getLibros().contains(libro)) {
            throw new RuntimeException("El libro ya está en el carrito");
        }

        carrito.getLibros().add(libro);
        carritoRepository.save(carrito);
        return "Agregado el libro al carrito";
    }


  /*  public CarritoEntity agregarLibro(Integer idCarrito, Integer idLibro) {
        CarritoEntity carrito = carritoRepository.findById(idCarrito)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        LibroEntity libro = libroRepository.findById(idLibro)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado: " + idLibro));

        if (carrito.getLibros().contains(libro)) {
            throw new RuntimeException("El libro ya está en el carrito");
        }

        carrito.getLibros().add(libro);
        return carritoRepository.save(carrito);
    }
*/
    @PreAuthorize("hasAuthority('VER_CARRITO')")
    public Optional<CarritoEntity> buscarPorIdUsuario(Integer idUsuario) {
        return carritoRepository.findByUsuarioId(idUsuario);
    }

    private CarritoDTO mapCarritoToDto(CarritoEntity carrito) {
        CarritoDTO dto = modelMapper.map(carrito, CarritoDTO.class);
        UsuarioCarritoDTO usuarioDTO = modelMapper.map(carrito.getUsuario(), UsuarioCarritoDTO.class);
        dto.setUsuario(usuarioDTO);
        return dto;
    }


    public Optional<CarritoEntity> obtenerCarritoConPrecioActualizadoPorUsuario(Integer idUsuario) {
        Optional<CarritoEntity> carritoOpt = carritoRepository.findByUsuarioId(idUsuario);

        if (carritoOpt.isPresent()) {
            CarritoEntity carrito = carritoOpt.get();


            double total = carrito.getLibros()
                    .stream()
                    .mapToDouble(LibroEntity::getPrecio)
                    .sum();


            carrito.setPrecio(total);

            return Optional.of(carrito);
        } else {
            return Optional.empty();
        }
    }

    public UsuarioEntity obtenerUsuarioPorCarritoId(Integer idCarrito) {
        CarritoEntity carrito = carritoRepository.findById(idCarrito)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado con id: " + idCarrito));
        return carrito.getUsuario();
    }



    @PreAuthorize("hasAuthority('VER_TODOS_LOS_CARRITOS')")
    public CarritoDTO2 MostrarDTOcarritoPorUsuario(Integer idUsuario) {
        CarritoEntity carritoOpt = carritoRepository.findByUsuarioId(idUsuario).orElseThrow(()-> new CarritoInexistente("Carrito no encontrado con id: " + idUsuario));


            // Mapear libros a DTO liviano
            List<LibroCarritoDTO> librosDTO = carritoOpt.getLibros().stream()
                    .map(libro -> {
                        LibroCarritoDTO dto = new LibroCarritoDTO();
                        dto.setTitulo(libro.getTitulo());
                        dto.setPrecio(libro.getPrecio());
                        dto.setFechaLanzamiento(libro.getFecha_lanzamiento());
                        return dto;
                    })
                    .toList();

            // Calcular el total
            double total = librosDTO.stream()
                    .mapToDouble(LibroCarritoDTO::getPrecio)
                    .sum();

            // Mapear usuario a DTO
            UsuarioEntity usuario = carritoOpt.getUsuario();
            UsuarioCarritoDTO usuarioDTO = new UsuarioCarritoDTO();
            usuarioDTO.setId(usuario.getId());
            usuarioDTO.setNombre(usuario.getNombre());
            usuarioDTO.setEmail(usuario.getEmail());

            // Crear CarritoDTO
            CarritoDTO2 carritoDTO = new CarritoDTO2();
            carritoDTO.setIdCarrito(carritoOpt.getIdCarrito());
            carritoDTO.setPrecio(total);
            carritoDTO.setLibros(librosDTO);
            carritoDTO.setUsuario(usuarioDTO);

            return carritoDTO;


    }

    public CarritoDTO2 MostrarDTOcarrito() {
        // Obtener el usuario logueado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CredencialEntity credencial = (CredencialEntity) authentication.getPrincipal();
        Long userId = credencial.getId();

        // Buscar el carrito del usuario
        CarritoEntity carrito = carritoRepository.findByUsuarioId(Math.toIntExact(userId))
                .orElseThrow(() -> new CarritoInexistente("Carrito no encontrado con id de usuario: " + userId));

        // Mapear libros a DTO liviano
        List<LibroCarritoDTO> librosDTO = carrito.getLibros().stream()
                .map(libro -> {
                    LibroCarritoDTO dto = new LibroCarritoDTO();
                    dto.setTitulo(libro.getTitulo());
                    dto.setPrecio(libro.getPrecio());
                    dto.setFechaLanzamiento(libro.getFecha_lanzamiento());
                    return dto;
                })
                .toList();

        // Calcular el total
        double total = librosDTO.stream()
                .mapToDouble(LibroCarritoDTO::getPrecio)
                .sum();

        // Mapear usuario a DTO
        UsuarioEntity usuario = carrito.getUsuario();
        UsuarioCarritoDTO usuarioDTO = new UsuarioCarritoDTO();
        usuarioDTO.setId(usuario.getId());
        usuarioDTO.setNombre(usuario.getNombre());
        usuarioDTO.setEmail(usuario.getEmail());

        // Armar CarritoDTO2
        CarritoDTO2 carritoDTO = new CarritoDTO2();
        carritoDTO.setIdCarrito(carrito.getIdCarrito());
        carritoDTO.setPrecio(total);
        carritoDTO.setLibros(librosDTO);
        carritoDTO.setUsuario(usuarioDTO);

        return carritoDTO;
    }

}

