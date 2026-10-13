package org.utn.tpfinalprogramacion3.dtos;

public record AutorResumenDTO(
        Integer idAutor,
        String nombre,
        String apellido,
        long cantidadLibros
) {
}
