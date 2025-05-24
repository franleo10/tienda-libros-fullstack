package org.utn.tpfinalprogramacion3.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utn.tpfinalprogramacion3.dtos.FacturaDTO;
import org.utn.tpfinalprogramacion3.services.FacturaService;

import java.util.List;

@RestController
@RequestMapping("/factura")
@RequiredArgsConstructor
public class FacturaEntityController {

    private final FacturaService facturaService;

    @PostMapping("/crear")
    public ResponseEntity<FacturaDTO> crearFactura(@RequestBody FacturaDTO facturaDTO) {
        FacturaDTO creada = facturaService.crearFactura(facturaDTO);
        return ResponseEntity.ok(creada);
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<FacturaDTO> obtenerPorId(@PathVariable Integer id) {
        FacturaDTO dto = facturaService.obtenerFacturaPorId(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/listar/usuario/{usuarioId}")
    public ResponseEntity<List<FacturaDTO>> obtenerFacturasPorUsuario(@PathVariable Integer usuarioId) {
        List<FacturaDTO> facturas = facturaService.obtenerFacturasPorUsuario(usuarioId);
        return ResponseEntity.ok(facturas);
    }


    @GetMapping("/listar")
    public ResponseEntity<List<FacturaDTO>> obtenerTodas() {
        return ResponseEntity.ok(facturaService.obtenerTodas());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<FacturaDTO> actualizar(@PathVariable Integer id, @RequestBody FacturaDTO facturaDTO) {
        FacturaDTO actualizada = facturaService.actualizarFactura(id, facturaDTO);
        return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        facturaService.eliminarFactura(id);
        return ResponseEntity.noContent().build();
    }
}

