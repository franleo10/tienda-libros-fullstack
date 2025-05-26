package org.utn.tpfinalprogramacion3.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.entities.CarritoEntity;
import org.utn.tpfinalprogramacion3.services.CarritoService;
import org.utn.tpfinalprogramacion3.services.CompraService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PostControlles {

    @Autowired
    private CarritoService carritoService;
    @Autowired
    private CompraService compraService;

    @GetMapping("/mercado")
    public String mercado(@RequestParam int idUsuario) throws MPException, MPApiException {

        MercadoPagoConfig.setAccessToken("TEST-1254104741812639-052309-36b18e46efe1566c8c5109bafca5d5da-1020339839");

        Optional<CarritoEntity> carritoOpt = carritoService.obtenerCarritoConPrecioActualizadoPorUsuario(idUsuario);

        if (carritoOpt.isEmpty()) {
            throw new RuntimeException("Carrito no encontrado para el usuario ID: " + idUsuario);
        }

        CarritoEntity carrito = carritoOpt.get();

        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .id("carrito-" + carrito.getIdCarrito())
                .title("Compra de libros")
                .description("Libros seleccionados del carrito")
                .quantity(1)
                .currencyId("ARS")
                .unitPrice(BigDecimal.valueOf(carrito.getPrecio()))
                .build();

        List<PreferenceItemRequest> items = List.of(itemRequest);

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success("https://www.google.com")
                .pending("https://www.google.com")
                .failure("https://www.google.com")
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .notificationUrl("https://153e-2800-2242-4080-a5e-6d31-f30c-fc21-5141.ngrok-free.app/api/webhook")
                .externalReference("carrito-" + carrito.getIdCarrito())
                .build();



        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        return preference.getInitPoint();
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> recibirWebhook(HttpServletRequest request) throws IOException {
        String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        System.out.println("Webhook recibido: " + body);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(body);

        if (jsonNode.has("type") && "payment".equals(jsonNode.get("type").asText())) {
            JsonNode dataNode = jsonNode.get("data");
            if (dataNode != null && dataNode.has("id")) {
                Long paymentId = dataNode.get("id").asLong();

                // Aquí consultamos el pago con Mercado Pago
                com.mercadopago.client.payment.PaymentClient paymentClient = new com.mercadopago.client.payment.PaymentClient();
                com.mercadopago.resources.payment.Payment payment = null;
                try {
                    payment = paymentClient.get(paymentId);
                } catch (MPException | MPApiException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(500).body("Error al obtener pago");
                }

                if ("approved".equalsIgnoreCase(payment.getStatus())) {
                    // Extraer idCarrito del campo external_reference o metadata si lo configuraste
                    // Por ejemplo, si pusiste "carrito-123" en external_reference:
                    String externalRef = payment.getExternalReference(); // o metadata

                    Integer idCarrito = null;
                    if (externalRef != null && externalRef.startsWith("carrito-")) {
                        try {
                            idCarrito = Integer.parseInt(externalRef.split("-")[1]);
                        } catch (NumberFormatException ex) {
                            System.out.println("Error parseando idCarrito de external_reference");
                        }
                    }

                    if (idCarrito != null) {
                        compraService.moverLibrosDelCarritoABiblioteca(idCarrito);
                        System.out.println("Compra procesada correctamente para carrito ID: " + idCarrito);
                    } else {
                        System.out.println("No se pudo obtener idCarrito para procesar compra.");
                    }
                }
            }
        }

        return ResponseEntity.ok("OK");
    }

}
