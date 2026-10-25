package org.utn.tpfinalprogramacion3.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.utn.tpfinalprogramacion3.enums.Rol;

// Fila de la tabla de usuarios del panel de administración
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UsuarioAdminDTO {

    private int id;
    private String nombre;
    private String email;
    private Rol rol;
    private boolean activo;
}
