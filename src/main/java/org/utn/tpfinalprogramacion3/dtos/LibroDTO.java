package org.utn.tpfinalprogramacion3.dtos;

import lombok.Getter;
import lombok.Setter;
import org.utn.tpfinalprogramacion3.entities.AutorEntity;
import org.utn.tpfinalprogramacion3.entities.GeneroEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class LibroDTO {
    private String titulo;
    private String sinopsis;
    private Float precio;
    private LocalDate fecha_lanzamiento;
    private List<AutorDTO> autores=new ArrayList<>();
    private List<GeneroDTO> generos=new ArrayList<>();
    private List<ReseniaDTOLibro>resenias=new ArrayList<>();

}