package org.utn.tpfinalprogramacion3.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class ReseniaDTO {
    private String texto;
    private BigDecimal calificacion;
    private LocalDate fecha;
    private UsuarioReseniaDTO usuario;
    private LibroTituloDTO libro;
}
