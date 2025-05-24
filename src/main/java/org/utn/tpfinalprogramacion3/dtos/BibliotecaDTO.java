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
public class BibliotecaDTO {
    private int idUsuario;
    private String nombreUsuario;
    private List<LibroDTO> libros;
}


