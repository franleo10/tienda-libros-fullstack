package org.utn.tpfinalprogramacion3.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class GeneroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idGenero;

    @Column(unique = true, name = "nombre", nullable = false, length = 50)
    private String nombre;


    @ManyToMany
    @JoinTable(
            name = "libroXgenero",
            joinColumns = @JoinColumn(name = "id_genero"),
            inverseJoinColumns = @JoinColumn(name = "id_libro")
    )
    private List<LibroEntity> libros;

}
