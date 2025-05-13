package org.utn.tpfinalprogramacion3.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class AutorDTO {

    private String nombre;
    private String apellido;

}
