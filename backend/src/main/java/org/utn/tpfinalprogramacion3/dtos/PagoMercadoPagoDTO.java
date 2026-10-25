package org.utn.tpfinalprogramacion3.dtos;

import java.math.BigDecimal;

/**
 * Datos de un pago de MercadoPago que necesita la tienda para procesar una compra.
 */
public record PagoMercadoPagoDTO(
        Long idPago,
        String estado,
        String externalReference,
        BigDecimal monto,
        String tipoPago
) {
}
