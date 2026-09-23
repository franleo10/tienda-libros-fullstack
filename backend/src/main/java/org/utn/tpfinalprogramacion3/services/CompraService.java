package org.utn.tpfinalprogramacion3.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.entities.BibliotecaEntity;
import org.utn.tpfinalprogramacion3.entities.CarritoEntity;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.repository.BibliotecaRepository;
import org.utn.tpfinalprogramacion3.repository.CarritoRepository;
import org.utn.tpfinalprogramacion3.repository.LibroRepository;

import java.util.HashSet;
import java.util.Optional;

@Service
public class CompraService {

    private final CarritoRepository carritoRepository;
    private final BibliotecaRepository bibliotecaRepository;
    private final LibroRepository libroRepository;
    private final OpenLibraryService openLibraryService;

    public CompraService(CarritoRepository carritoRepository, BibliotecaRepository bibliotecaRepository, LibroRepository libroRepository, OpenLibraryService openLibraryService) {
        this.carritoRepository = carritoRepository;
        this.bibliotecaRepository = bibliotecaRepository;
        this.libroRepository = libroRepository;
        this.openLibraryService = openLibraryService;

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

        for (LibroEntity libro : carrito.getLibros()) {
            if (libro.getUrlPdf() == null || libro.getUrlPdf().isEmpty()) {
                String urlPdf = openLibraryService.obtenerUrlLibroPorTitulo(libro.getTitulo()).block();
                if (urlPdf != null) {
                    libro.setUrlPdf(urlPdf);
                    libroRepository.save(libro);
                }
            }
            biblioteca.getLibros().add(libro);
        }

        carrito.getLibros().clear();
        carrito.setPrecio(0.0);

        bibliotecaRepository.save(biblioteca);
        carritoRepository.save(carrito);
    }



}
