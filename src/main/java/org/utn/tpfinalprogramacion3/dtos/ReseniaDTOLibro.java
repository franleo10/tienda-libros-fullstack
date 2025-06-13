package org.utn.tpfinalprogramacion3.dtos;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReseniaDTOLibro {
    private Long id;
    private String comentario;
    private int calificacion;
    private String nombreUsuario;
}
