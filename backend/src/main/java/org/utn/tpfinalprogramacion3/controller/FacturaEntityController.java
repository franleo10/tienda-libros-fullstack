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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/factura")
@RequiredArgsConstructor
public class FacturaEntityController {

    private final FacturaService facturaService;

    @Operation(summary = "Obtener facturas paginadas por usuario", description = "Obtiene una página de facturas asociadas al usuario autenticado, según el número de página indicado.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @GetMapping("/usuario/pag/{numeroPaginacion}")
    public ResponseEntity<Page<FacturaResponseDTO>> obtenerFacturasPorUsuarioID(@PathVariable int numeroPaginacion) {
        return ResponseEntity.ok(facturaService.obtenerFacturasPorUsuario(numeroPaginacion));
    }

    @Operation(summary = "Obtener facturas paginadas por usuario (Admin)", description = "Obtiene una página de facturas asociadas a un usuario específico, indicado por su ID, para uso administrativo.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @GetMapping("/usuario/{idUsuario}/pag/{numeroPaginacion}")
    public ResponseEntity<Page<FacturaResponseDTO>> obtenerFacturasPorUsuarioID(@PathVariable int idUsuario,
            @PathVariable int numeroPaginacion) {
        return ResponseEntity.ok(facturaService.obtenerFacturasPorUsuarioParaAdmin(idUsuario, numeroPaginacion));
    }

}
