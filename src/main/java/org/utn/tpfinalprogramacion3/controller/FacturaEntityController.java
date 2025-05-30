package org.utn.tpfinalprogramacion3.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.dtos.FacturaDTO;
import org.utn.tpfinalprogramacion3.dtos.FacturaResponseDTO;
import org.utn.tpfinalprogramacion3.entities.FacturaEntity;
import org.utn.tpfinalprogramacion3.services.FacturaService;

import java.util.List;

@RestController
@RequestMapping("/factura")
@RequiredArgsConstructor
public class FacturaEntityController {

    private final FacturaService facturaService;

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<FacturaResponseDTO>> obtenerFacturasPorUsuario(@PathVariable int idUsuario) {
        return ResponseEntity.ok(facturaService.obtenerFacturasPorUsuario(idUsuario));
    }
}

