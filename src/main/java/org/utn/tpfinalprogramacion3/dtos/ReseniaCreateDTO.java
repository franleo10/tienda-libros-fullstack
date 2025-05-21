package org.utn.tpfinalprogramacion3.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReseniaCreateDTO {

    private String texto;
    private BigDecimal calificacion;
    private UsuarioReseniaDTO usuario;
    private LibroReseniaIdTitulo libro;

}
