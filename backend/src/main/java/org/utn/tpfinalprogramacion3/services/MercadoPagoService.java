package org.utn.tpfinalprogramacion3.services;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.utn.tpfinalprogramacion3.dtos.PagoMercadoPagoDTO;
import org.utn.tpfinalprogramacion3.entities.CarritoEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * Único punto de contacto con el SDK de MercadoPago.
 */
@Service
public class MercadoPagoService {

    private static final String ACCESS_TOKEN = "TEST-1254104741812639-052309-36b18e46efe1566c8c5109bafca5d5da-1020339839";

    // A donde vuelve el comprador después de pagar. MercadoPago agrega payment_id, status, etc. como query params.
    private static final String URL_RETORNO = "http://localhost:4200/compra-confirmada";

    // Solo funciona si el backend es accesible desde internet (por ejemplo con ngrok).
    private static final String URL_WEBHOOK = "https://bd47-2800-2242-40c0-c60a-78cd-ba52-f0b0-acfd.ngrok-free.app/api/webhook";

    public MercadoPagoService() {
        MercadoPagoConfig.setAccessToken(ACCESS_TOKEN);
    }

    public String crearPreferencia(CarritoEntity carrito) throws MPException, MPApiException {
        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .id("carrito-" + carrito.getIdCarrito())
                .title("Compra de libros")
                .description("Libros seleccionados del carrito")
                .quantity(1)
                .currencyId("ARS")
                .unitPrice(BigDecimal.valueOf(carrito.getPrecio()))
                .build();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(URL_RETORNO)
                .pending(URL_RETORNO)
                .failure(URL_RETORNO)
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(List.of(itemRequest))
                .backUrls(backUrls)
                .notificationUrl(URL_WEBHOOK)
                .externalReference("carrito-" + carrito.getIdCarrito())
                .build();

        return new PreferenceClient().create(preferenceRequest).getInitPoint();
    }

    public PagoMercadoPagoDTO obtenerPago(Long idPago) {
        try {
            Payment pago = new PaymentClient().get(idPago);
            return new PagoMercadoPagoDTO(
                    pago.getId(),
                    pago.getStatus(),
                    pago.getExternalReference(),
                    pago.getTransactionAmount(),
                    pago.getPaymentTypeId()
            );
        } catch (MPApiException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el pago indicado en MercadoPago.");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar el pago en MercadoPago.");
        } catch (MPException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar el pago en MercadoPago.");
        }
    }
}
