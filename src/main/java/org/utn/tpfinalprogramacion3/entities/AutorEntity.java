package org.utn.tpfinalprogramacion3.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "autores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAutor;
    @Column(name="nombre", nullable = false, length = 100)
    private String nombre;
    @Column(name="apellido", nullable = false, length = 100)
    private String apellido;

    //@OneToMany(mappedBy = "autor")
    //private List<AutorXLibro> libros;
}

