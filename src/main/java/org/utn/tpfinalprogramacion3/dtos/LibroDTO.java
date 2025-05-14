package org.utn.tpfinalprogramacion3.dtos;

import org.utn.tpfinalprogramacion3.entities.AutorEntity;
import org.utn.tpfinalprogramacion3.entities.GeneroEntity;

import java.time.LocalDate;
import java.util.List;

public class LibroDTO {
    private String titulo;
    private String sinopsis;
    private Float precio;
    private LocalDate fecha_lanzamiento;
    private List<AutorEntity> autores;
    private List<GeneroEntity> generos;
}
