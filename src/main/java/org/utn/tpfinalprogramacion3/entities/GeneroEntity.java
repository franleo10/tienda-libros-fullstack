package org.utn.tpfinalprogramacion3.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
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


    @ManyToMany(mappedBy = "generos", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<LibroEntity> libros = new ArrayList<>();
}
