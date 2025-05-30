package org.utn.tpfinalprogramacion3.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.dtos.CarritoDTO;
import org.utn.tpfinalprogramacion3.dtos.UsuarioCarritoDTO;
import org.utn.tpfinalprogramacion3.entities.CarritoEntity;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.repository.CarritoRepository;
import org.utn.tpfinalprogramacion3.repository.LibroRepository;
import org.utn.tpfinalprogramacion3.repository.UsuarioRepository;

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

    public List<CarritoDTO> listarTodos() {
        return carritoRepository.findAll()
                .stream()
                .map(this::mapCarritoToDto)
                .collect(Collectors.toList());
    }


    public Optional<CarritoEntity> buscarPorId(Integer id) {
        return carritoRepository.findById(id);
    }

    public void eliminar(Integer id) {
        carritoRepository.deleteById(id);
    }

    public CarritoEntity agregarLibro(Integer idCarrito, Integer idLibro) {
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


}

