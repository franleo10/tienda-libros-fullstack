package org.utn.tpfinalprogramacion3.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarritoDTO2 {
    private Integer idCarrito;
    private double precio;
    private UsuarioCarritoDTO usuario;
    private List<LibroCarritoDTO> libros;

}
