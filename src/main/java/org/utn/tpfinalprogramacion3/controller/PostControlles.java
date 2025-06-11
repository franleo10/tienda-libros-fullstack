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
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.repository.CarritoRepository;
import org.utn.tpfinalprogramacion3.services.CarritoService;
import org.utn.tpfinalprogramacion3.services.CompraService;
import org.utn.tpfinalprogramacion3.services.FacturaService;

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
    @Autowired
    private FacturaService facturaService;
    @Autowired
    private CarritoRepository carritoRepository;

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
                .notificationUrl("https://3a89-2800-2242-40c0-c60a-3956-d206-95d2-9ed7.ngrok-free.app/api/webhook")
                .externalReference("carrito-" + carrito.getIdCarrito())
                .build();



        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        return preference.getInitPoint();
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> recibirWebhook(@RequestBody String body) {
        try {
            System.out.println("Webhook recibido: " + body);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(body);

            if (jsonNode.has("type") && "payment".equals(jsonNode.get("type").asText())) {
                JsonNode dataNode = jsonNode.path("data");
                String paymentIdStr = dataNode.path("id").asText();

                if (paymentIdStr != null && !paymentIdStr.isEmpty()) {
                    Long paymentId = Long.parseLong(paymentIdStr);

                    // Obtener el pago desde Mercado Pago
                    var paymentClient = new com.mercadopago.client.payment.PaymentClient();
                    var payment = paymentClient.get(paymentId);

                    if ("approved".equalsIgnoreCase(payment.getStatus())) {
                        String externalRef = payment.getExternalReference();
                        if (externalRef != null && externalRef.startsWith("carrito-")) {
                            int idCarrito = Integer.parseInt(externalRef.split("-")[1]);

                            var carrito = carritoRepository.findByIdConLibros(idCarrito)
                                    .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

                            List<String> titulosLibros = carrito.getLibros()
                                    .stream()
                                    .map(libro -> libro.getTitulo())
                                    .toList();

                            compraService.moverLibrosDelCarritoABiblioteca(idCarrito);
                            System.out.println("✅ Compra procesada para carrito ID: " + idCarrito);

                            UsuarioEntity usuario = carritoService.obtenerUsuarioPorCarritoId(idCarrito);
                            facturaService.generarFactura(payment.getTransactionAmount().doubleValue(),payment.getExternalReference(),payment.getPaymentTypeId(), usuario.getId(),titulosLibros);

                        }


                    }
                }
            }

            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error en webhook: " + e.getMessage());
        }
    }


}
