package org.utn.tpfinalprogramacion3.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.utn.tpfinalprogramacion3.Exceptions.PagoNoAprobadoException;
import org.utn.tpfinalprogramacion3.dtos.FacturaResponseDTO;
import org.utn.tpfinalprogramacion3.dtos.PagoMercadoPagoDTO;
import org.utn.tpfinalprogramacion3.entities.CarritoEntity;
import org.utn.tpfinalprogramacion3.entities.FacturaEntity;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.repository.CarritoRepository;
import org.utn.tpfinalprogramacion3.repository.FacturaRepository;
import org.utn.tpfinalprogramacion3.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PagoServiceTest {

    private MercadoPagoService mercadoPago;
    private CompraService compraService;
    private FacturaRepository facturas;
    private CarritoRepository carritos;
    private UsuarioRepository usuarios;
    private PagoService service;
    private UsuarioEntity usuario;

    @BeforeEach
    void preparar() {
        mercadoPago = mock(MercadoPagoService.class);
        compraService = mock(CompraService.class);
        facturas = mock(FacturaRepository.class);
        carritos = mock(CarritoRepository.class);
        usuarios = mock(UsuarioRepository.class);
        FacturaService facturaService = new FacturaService(facturas, usuarios, carritos, new ModelMapper());
        service = new PagoService(mercadoPago, compraService, facturaService, facturas, carritos);

        usuario = new UsuarioEntity();
        usuario.setId(7);
        when(usuarios.findById(7)).thenReturn(Optional.of(usuario));
        when(facturas.save(any(FacturaEntity.class))).thenAnswer(invocacion -> {
            FacturaEntity guardada = invocacion.getArgument(0);
            guardada.setIdFactura(10);
            return guardada;
        });
    }

    @Test
    void pagoAprobadoMueveLosLibrosYGeneraLaFactura() {
        when(facturas.findByIdPago(555L)).thenReturn(Optional.empty());
        when(mercadoPago.obtenerPago(555L)).thenReturn(pago("approved"));
        when(carritos.findById(3)).thenReturn(Optional.of(carritoCon("Rayuela", "Bestiario")));

        FacturaResponseDTO factura = service.procesarPagoAprobado(555L, 7);

        verify(compraService).moverLibrosDelCarritoABiblioteca(3);
        assertEquals(21500.0, factura.getMonto());
        assertEquals("credit_card", factura.getMetodoPago());
        assertEquals(List.of("Rayuela", "Bestiario"), factura.getTitulosLibrosComprados());
        assertEquals(7, factura.getIdUsuario());
    }

    @Test
    void pagoYaProcesadoDevuelveLaFacturaSinVolverAProcesar() {
        FacturaEntity existente = FacturaEntity.builder()
                .idFactura(10)
                .idPago(555L)
                .monto(21500.0)
                .fechaCompra(LocalDateTime.now())
                .metodoPago("credit_card")
                .externalReference("carrito-3")
                .usuario(usuario)
                .librosComprados("Rayuela, Bestiario")
                .build();
        when(facturas.findByIdPago(555L)).thenReturn(Optional.of(existente));

        FacturaResponseDTO factura = service.procesarPagoAprobado(555L, 7);

        assertEquals(10, factura.getIdFactura());
        verify(mercadoPago, never()).obtenerPago(any());
        verify(compraService, never()).moverLibrosDelCarritoABiblioteca(anyInt());
        verify(facturas, never()).save(any());
    }

    @Test
    void pagoPendienteNoSeProcesa() {
        when(facturas.findByIdPago(555L)).thenReturn(Optional.empty());
        when(mercadoPago.obtenerPago(555L)).thenReturn(pago("pending"));

        PagoNoAprobadoException error = assertThrows(PagoNoAprobadoException.class,
                () -> service.procesarPagoAprobado(555L, 7));

        assertEquals("Tu pago todavía está pendiente de aprobación.", error.getMessage());
        verify(compraService, never()).moverLibrosDelCarritoABiblioteca(anyInt());
    }

    @Test
    void noSePuedeConfirmarLaCompraDeOtroUsuario() {
        when(facturas.findByIdPago(555L)).thenReturn(Optional.empty());
        when(mercadoPago.obtenerPago(555L)).thenReturn(pago("approved"));
        when(carritos.findById(3)).thenReturn(Optional.of(carritoCon("Rayuela")));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.procesarPagoAprobado(555L, 99));

        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verify(compraService, never()).moverLibrosDelCarritoABiblioteca(anyInt());
    }

    private PagoMercadoPagoDTO pago(String estado) {
        return new PagoMercadoPagoDTO(555L, estado, "carrito-3", new BigDecimal("21500"), "credit_card");
    }

    private CarritoEntity carritoCon(String... titulos) {
        List<LibroEntity> libros = new ArrayList<>();
        for (String titulo : titulos) {
            LibroEntity libro = new LibroEntity();
            libro.setTitulo(titulo);
            libros.add(libro);
        }
        CarritoEntity carrito = new CarritoEntity();
        carrito.setIdCarrito(3);
        carrito.setUsuario(usuario);
        carrito.setLibros(libros);
        return carrito;
    }
}
