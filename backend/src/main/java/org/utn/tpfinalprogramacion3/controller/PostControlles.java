package org.utn.tpfinalprogramacion3.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.Exceptions.PagoNoAprobadoException;
import org.utn.tpfinalprogramacion3.dtos.FacturaResponseDTO;
import org.utn.tpfinalprogramacion3.entities.CarritoEntity;
import org.utn.tpfinalprogramacion3.security.entities.CredencialEntity;
import org.utn.tpfinalprogramacion3.services.CarritoService;
import org.utn.tpfinalprogramacion3.services.MercadoPagoService;
import org.utn.tpfinalprogramacion3.services.PagoService;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PostControlles {

    @Autowired
    private CarritoService carritoService;
    @Autowired
    private MercadoPagoService mercadoPagoService;
    @Autowired
    private PagoService pagoService;

    @Operation(
            summary = "Generar preferencia de pago en Mercado Pago",
            description = "Recibe un ID de usuario, genera una preferencia de pago en Mercado Pago en base al carrito asociado y retorna el enlace de inicio de pago (init_point).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Enlace generado correctamente", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Carrito no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error interno al generar la preferencia")
            }
    )
    @GetMapping("/mercado")
    public String mercado(@RequestParam int idUsuario) throws MPException, MPApiException {

        Optional<CarritoEntity> carritoOpt = carritoService.obtenerCarritoConPrecioActualizadoPorUsuario(idUsuario);

        if (carritoOpt.isEmpty()) {
            throw new RuntimeException("Carrito no encontrado para el usuario ID: " + idUsuario);
        }

        return mercadoPagoService.crearPreferencia(carritoOpt.get());
    }

    @Operation(
            summary = "Confirmar una compra al volver de Mercado Pago",
            description = "Verifica con Mercado Pago que el pago esté aprobado, mueve los libros del carrito a la biblioteca y devuelve la factura. Si el pago ya fue procesado (por ejemplo por el webhook), devuelve la factura existente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Compra confirmada", content = @Content(schema = @Schema(implementation = FacturaResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "La compra pertenece a otro usuario"),
                    @ApiResponse(responseCode = "404", description = "Pago o carrito no encontrado"),
                    @ApiResponse(responseCode = "409", description = "El pago no está aprobado")
            }
    )
    @PostMapping("/pagos/{idPago}/confirmar")
    public ResponseEntity<FacturaResponseDTO> confirmarPago(@PathVariable Long idPago, Authentication authentication) {
        CredencialEntity credencial = (CredencialEntity) authentication.getPrincipal();
        return ResponseEntity.ok(pagoService.procesarPagoAprobado(idPago, credencial.getUsuario().getId()));
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> recibirWebhook(@RequestBody String body) {
        try {
            System.out.println("Webhook recibido: " + body);

            JsonNode jsonNode = new ObjectMapper().readTree(body);

            if (jsonNode.has("type") && "payment".equals(jsonNode.get("type").asText())) {
                String paymentIdStr = jsonNode.path("data").path("id").asText();

                if (paymentIdStr != null && !paymentIdStr.isEmpty()) {
                    pagoService.procesarPagoAprobado(Long.parseLong(paymentIdStr), null);
                    System.out.println("✅ Compra procesada para el pago ID: " + paymentIdStr);
                }
            }

            return ResponseEntity.ok("OK");
        } catch (PagoNoAprobadoException e) {
            // MercadoPago también avisa pagos pendientes o rechazados: no hay nada que procesar
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error en webhook: " + e.getMessage());
        }
    }


}
