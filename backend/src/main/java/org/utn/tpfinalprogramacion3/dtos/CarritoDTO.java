package org.utn.tpfinalprogramacion3.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class CarritoDTO {
    private Integer idCarrito;
    private Integer idUsuario;
    private UsuarioCarritoDTO usuario;
    private List<LibroDTO> libros;
}

