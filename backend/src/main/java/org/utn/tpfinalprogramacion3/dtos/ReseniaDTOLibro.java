package org.utn.tpfinalprogramacion3.dtos;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReseniaDTOLibro {
    private Long id;
    private String texto;
    private BigDecimal calificacion;
    private String nombreUsuario;
}
