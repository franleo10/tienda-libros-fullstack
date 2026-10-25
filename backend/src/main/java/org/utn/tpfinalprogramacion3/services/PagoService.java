package org.utn.tpfinalprogramacion3.services;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.utn.tpfinalprogramacion3.Exceptions.CarritoInexistente;
import org.utn.tpfinalprogramacion3.Exceptions.PagoNoAprobadoException;
import org.utn.tpfinalprogramacion3.dtos.FacturaResponseDTO;
import org.utn.tpfinalprogramacion3.dtos.PagoMercadoPagoDTO;
import org.utn.tpfinalprogramacion3.entities.CarritoEntity;
import org.utn.tpfinalprogramacion3.entities.FacturaEntity;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.repository.CarritoRepository;
import org.utn.tpfinalprogramacion3.repository.FacturaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Procesa los pagos aprobados de MercadoPago: mueve los libros del carrito a la biblioteca y genera la factura.
 * Lo usan tanto el webhook como la pantalla de confirmación, y cada pago se procesa una sola vez.
 */
@Service
public class PagoService {

    private static final String PREFIJO_REFERENCIA = "carrito-";

    private final MercadoPagoService mercadoPagoService;
    private final CompraService compraService;
    private final FacturaService facturaService;
    private final FacturaRepository facturaRepository;
    private final CarritoRepository carritoRepository;

    public PagoService(MercadoPagoService mercadoPagoService, CompraService compraService,
            FacturaService facturaService, FacturaRepository facturaRepository,
            CarritoRepository carritoRepository) {
        this.mercadoPagoService = mercadoPagoService;
        this.compraService = compraService;
        this.facturaService = facturaService;
        this.facturaRepository = facturaRepository;
        this.carritoRepository = carritoRepository;
    }

    /**
     * @param idUsuarioEsperado usuario que confirma la compra; null cuando llama el webhook de MercadoPago.
     */
    @Transactional
    public FacturaResponseDTO procesarPagoAprobado(Long idPago, Integer idUsuarioEsperado) {
        Optional<FacturaEntity> facturaExistente = facturaRepository.findByIdPago(idPago);
        if (facturaExistente.isPresent()) {
            FacturaEntity factura = facturaExistente.get();
            validarDuenio(factura.getUsuario().getId(), idUsuarioEsperado);
            return facturaService.mapFacturaToDTO(factura);
        }

        PagoMercadoPagoDTO pago = mercadoPagoService.obtenerPago(idPago);
        if (!"approved".equalsIgnoreCase(pago.estado())) {
            throw new PagoNoAprobadoException(mensajeEstado(pago.estado()));
        }

        int idCarrito = idCarritoDesdeReferencia(pago.externalReference());
        CarritoEntity carrito = carritoRepository.findById(idCarrito)
                .orElseThrow(() -> new CarritoInexistente("No se encontró el carrito de esta compra."));
        int idUsuario = carrito.getUsuario().getId();
        validarDuenio(idUsuario, idUsuarioEsperado);

        // Se toman los títulos antes de vaciar el carrito
        List<String> titulos = carrito.getLibros().stream()
                .map(LibroEntity::getTitulo)
                .toList();

        compraService.moverLibrosDelCarritoABiblioteca(idCarrito);
        FacturaEntity factura = facturaService.generarFactura(
                pago.monto().doubleValue(),
                pago.externalReference(),
                pago.tipoPago(),
                idUsuario,
                titulos,
                idPago
        );
        return facturaService.mapFacturaToDTO(factura);
    }

    private void validarDuenio(int idUsuarioCompra, Integer idUsuarioEsperado) {
        if (idUsuarioEsperado != null && idUsuarioCompra != idUsuarioEsperado) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta compra pertenece a otro usuario.");
        }
    }

    private int idCarritoDesdeReferencia(String externalReference) {
        if (externalReference == null || !externalReference.startsWith(PREFIJO_REFERENCIA)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pago no corresponde a una compra de la tienda.");
        }
        try {
            return Integer.parseInt(externalReference.substring(PREFIJO_REFERENCIA.length()));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pago no corresponde a una compra de la tienda.");
        }
    }

    private String mensajeEstado(String estado) {
        if ("pending".equalsIgnoreCase(estado) || "in_process".equalsIgnoreCase(estado)) {
            return "Tu pago todavía está pendiente de aprobación.";
        }
        if ("rejected".equalsIgnoreCase(estado) || "cancelled".equalsIgnoreCase(estado)) {
            return "Tu pago fue rechazado o cancelado.";
        }
        return "El pago no fue aprobado.";
    }
}
