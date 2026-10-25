package org.utn.tpfinalprogramacion3.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.utn.tpfinalprogramacion3.dtos.FacturaResponseDTO;
import org.utn.tpfinalprogramacion3.entities.FacturaEntity;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.repository.CarritoRepository;
import org.utn.tpfinalprogramacion3.repository.FacturaRepository;
import org.utn.tpfinalprogramacion3.repository.UsuarioRepository;
import org.utn.tpfinalprogramacion3.security.entities.CredencialEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FacturaServiceTest {

    private FacturaRepository facturas;
    private FacturaService service;
    private UsuarioEntity usuarioLogueado;

    @BeforeEach
    void setUp() {
        facturas = mock(FacturaRepository.class);
        service = new FacturaService(facturas, mock(UsuarioRepository.class),
                mock(CarritoRepository.class), new ModelMapper());

        // Ids distintos a propósito: el usuario se toma de credencial.getUsuario(), no del id de la credencial
        usuarioLogueado = usuario(7);
        CredencialEntity credencial = new CredencialEntity();
        credencial.setId(99L);
        credencial.setUsuario(usuarioLogueado);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(credencial, null, List.of()));
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void historialPideLasComprasDelUsuarioDeLaMasRecienteALaMasAntigua() {
        FacturaEntity factura = factura(3, usuarioLogueado);
        when(facturas.findByUsuarioId(eq(7), any(Pageable.class)))
                .thenAnswer(inv -> new PageImpl<>(List.of(factura), inv.getArgument(1), 1));

        Page<FacturaResponseDTO> pagina = service.obtenerFacturasPorUsuario(0);

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(facturas).findByUsuarioId(eq(7), pageable.capture());
        Sort.Order orden = pageable.getValue().getSort().getOrderFor("fechaCompra");
        assertEquals(Sort.Direction.DESC, orden.getDirection());

        FacturaResponseDTO dto = pagina.getContent().get(0);
        assertEquals(3, dto.getIdFactura());
        assertEquals(1234L, dto.getIdPago());
        assertEquals(List.of("Rayuela", "Ficciones"), dto.getTitulosLibrosComprados());
    }

    @Test
    void historialSinComprasDevuelvePaginaVacia() {
        when(facturas.findByUsuarioId(eq(7), any(Pageable.class)))
                .thenAnswer(inv -> Page.empty(inv.getArgument(1)));

        assertTrue(service.obtenerFacturasPorUsuario(0).isEmpty());
    }

    @Test
    void detalleDevuelveUnaCompraPropia() {
        when(facturas.findById(3)).thenReturn(Optional.of(factura(3, usuarioLogueado)));

        FacturaResponseDTO dto = service.obtenerFacturaPropia(3);

        assertEquals(3, dto.getIdFactura());
        assertEquals(2500.0, dto.getMonto());
    }

    @Test
    void detalleDeUnaCompraAjenaRespondeComoSiNoExistiera() {
        when(facturas.findById(4)).thenReturn(Optional.of(factura(4, usuario(8))));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.obtenerFacturaPropia(4));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void detalleDeUnaCompraInexistenteResponde404() {
        when(facturas.findById(5)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.obtenerFacturaPropia(5));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    private static UsuarioEntity usuario(int id) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(id);
        return usuario;
    }

    private static FacturaEntity factura(int id, UsuarioEntity usuario) {
        return FacturaEntity.builder()
                .idFactura(id)
                .monto(2500.0)
                .fechaCompra(LocalDateTime.of(2026, 10, 20, 15, 30))
                .metodoPago("account_money")
                .externalReference("carrito-1")
                .idPago(1234L)
                .usuario(usuario)
                .librosComprados("Rayuela, Ficciones")
                .build();
    }
}
