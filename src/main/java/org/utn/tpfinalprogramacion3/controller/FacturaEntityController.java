package org.utn.tpfinalprogramacion3.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.dtos.FacturaDTO;
import org.utn.tpfinalprogramacion3.dtos.FacturaResponseDTO;
import org.utn.tpfinalprogramacion3.entities.FacturaEntity;
import org.utn.tpfinalprogramacion3.services.FacturaService;

import com.mercadopago.net.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/factura")
@RequiredArgsConstructor
public class FacturaEntityController {

    private final FacturaService facturaService;

    @GetMapping("/usuario/{idUsuario}/pag/{numeroPaginacion}")
    public ResponseEntity<Page<FacturaResponseDTO>> obtenerFacturasPorUsuarioID(@PathVariable int idUsuario,
            @PathVariable int numeroPaginacion) {
        return ResponseEntity.ok(facturaService.obtenerFacturasPorUsuario(idUsuario, numeroPaginacion));
    }
}
