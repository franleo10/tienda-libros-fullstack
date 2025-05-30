package org.utn.tpfinalprogramacion3.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class FacturaResponseDTO {

    private int idFactura;
    private Double monto;
    private LocalDateTime fechaCompra;
    private String metodoPago;
    private String externalReference;

    private Integer idUsuario;

    private List<String> titulosLibrosComprados;

}
