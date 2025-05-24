package org.utn.tpfinalprogramacion3.dtos;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaDTO {
    private Integer idFactura;
    private String descripcion;
    private LocalDate fechaCompra;
    private Integer metodoDePagoId;
    private Integer carritoId;
}

