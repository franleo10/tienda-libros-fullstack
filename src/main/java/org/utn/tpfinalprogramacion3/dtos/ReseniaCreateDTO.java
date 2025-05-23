package org.utn.tpfinalprogramacion3.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class ReseniaCreateDTO {

    private String texto;
    private BigDecimal calificacion;
    private UsuarioCreateReseniaDTO usuario;
    private LibroReseniaIdTitulo libro;

}
