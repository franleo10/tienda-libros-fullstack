package org.utn.tpfinalprogramacion3.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class LibroBibliotecaDTO {

        private int idLibro;
        private String titulo;
        private Float precio;
        private String urlPdf;
        private String urlPortada;
        private List<AutorDTO> autores;
        private boolean esFavorito;


}
