package org.utn.tpfinalprogramacion3.controller;


import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.utn.tpfinalprogramacion3.entities.CarritoEntity;
import org.utn.tpfinalprogramacion3.services.CarritoService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PostControlles {

    @Autowired
    private CarritoService carritoService;

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
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        return preference.getInitPoint();
    }

}
