package org.utn.tpfinalprogramacion3.dtos;

import java.util.List;

public record AutorDetalleDTO(
        Integer idAutor,
        String nombre,
        String apellido,
        List<LibroDTO> libros
) {
}
