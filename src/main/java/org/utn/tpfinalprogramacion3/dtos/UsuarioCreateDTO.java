package org.utn.tpfinalprogramacion3.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.utn.tpfinalprogramacion3.enums.Rol;

@NoArgsConstructor
@Getter
@Setter

public class UsuarioCreateDTO {
    private String nombre;
    private String email;
    private int edad;





}
