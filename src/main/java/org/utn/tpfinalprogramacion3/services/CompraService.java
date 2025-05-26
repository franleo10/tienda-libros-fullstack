package org.utn.tpfinalprogramacion3.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.entities.BibliotecaEntity;
import org.utn.tpfinalprogramacion3.entities.CarritoEntity;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.repository.BibliotecaRepository;
import org.utn.tpfinalprogramacion3.repository.CarritoRepository;

import java.util.HashSet;
import java.util.Optional;

@Service
public class CompraService {

    private final CarritoRepository carritoRepository;
    private final BibliotecaRepository bibliotecaRepository;

    public CompraService(CarritoRepository carritoRepository, BibliotecaRepository bibliotecaRepository) {
        this.carritoRepository = carritoRepository;
        this.bibliotecaRepository = bibliotecaRepository;
    }

    @Transactional
    public void moverLibrosDelCarritoABiblioteca(Integer idCarrito) {
        Optional<CarritoEntity> carritoOpt = carritoRepository.findById(idCarrito);
        if (carritoOpt.isEmpty()) {
            throw new RuntimeException("Carrito no encontrado con id: " + idCarrito);
        }
        CarritoEntity carrito = carritoOpt.get();

        UsuarioEntity usuario = carrito.getUsuario();


        BibliotecaEntity biblioteca = bibliotecaRepository.findByUsuario(usuario)
                .orElseGet(() -> {
                    BibliotecaEntity nuevaBiblioteca = new BibliotecaEntity();
                    nuevaBiblioteca.setUsuario(usuario);
                    nuevaBiblioteca.setLibros(new HashSet<>());
                    return bibliotecaRepository.save(nuevaBiblioteca);
                });


        biblioteca.getLibros().addAll(carrito.getLibros());


        carrito.getLibros().clear();
        carrito.setPrecio(0.0);


        bibliotecaRepository.save(biblioteca);
        carritoRepository.save(carrito);
    }



}
