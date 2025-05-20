package org.utn.tpfinalprogramacion3.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.dtos.CarritoDTO;
import org.utn.tpfinalprogramacion3.dtos.LibroDTO;
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
        this.modelMapper = modelMapper;
        this.usuarioRepository  = usuarioRepository;
    }

    public Optional<CarritoDTO> createCarrito(CarritoDTO carritoDTO, Integer idUsuario) {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Buscar si el usuario ya tiene un carrito
        Optional<CarritoEntity> carritoExistente = carritoRepository.findByUsuarioId(usuarioEntity.getId());


        // Si no tiene, creamos uno nuevo vacío
        CarritoEntity nuevoCarrito = new CarritoEntity();
        nuevoCarrito.setUsuario(usuarioEntity);
        nuevoCarrito.setLibros(List.of()); // vacío

        carritoRepository.save(nuevoCarrito);

        return Optional.of(modelMapper.map(carritoExistente, CarritoDTO.class));
    }

    public List<CarritoEntity> listarTodos() {
        return carritoRepository.findAll();
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

        // Verificar si el libro ya está en el carrito
        if (carrito.getLibros().contains(libro)) {
            throw new RuntimeException("El libro ya está en el carrito");
        }

        carrito.getLibros().add(libro);
        CarritoEntity actualizado = carritoRepository.save(carrito);

        return actualizado;
    }


    public Optional<CarritoEntity> buscarPorIdUsuario(Integer idUsuario) {
        return carritoRepository.findByUsuarioId(idUsuario);
    }



}

