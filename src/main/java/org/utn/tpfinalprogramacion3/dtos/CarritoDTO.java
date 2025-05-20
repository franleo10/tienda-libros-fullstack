package org.utn.tpfinalprogramacion3.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;

import java.util.List;

@Getter
@Setter
@Builder

public class CarritoDTO {
    private Integer idCarrito;
    private Integer idUsuario;
}

