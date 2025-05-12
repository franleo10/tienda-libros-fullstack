package org.utn.tpfinalprogramacion3.entities;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BibliotecaId implements Serializable {
    private Integer idUsuario;
    private Integer idLibro;
}
