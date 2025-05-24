package org.utn.tpfinalprogramacion3.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class LibroBibliotecaDTO {

        private int idLibro;
        private String titulo;
        private Float precio;


}
