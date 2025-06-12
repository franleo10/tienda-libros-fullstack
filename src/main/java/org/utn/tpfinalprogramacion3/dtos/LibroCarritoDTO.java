package org.utn.tpfinalprogramacion3.dtos;


import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LibroCarritoDTO {
    private String titulo;
    private double precio;
    private LocalDate fechaLanzamiento;

}
