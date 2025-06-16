package org.utn.tpfinalprogramacion3.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bibliotecas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BibliotecaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "id_usuario")
    @JsonBackReference
    private UsuarioEntity usuario;

    @ManyToMany
    @JoinTable(
            name = "biblioteca_libros",
            joinColumns = @JoinColumn(name = "id_biblioteca"),
            inverseJoinColumns = @JoinColumn(name = "id_libro")
    )
    private Set<LibroEntity> libros = new HashSet<>();


    @ManyToMany
    @JoinTable(
            name = "biblioteca_libros_favoritos",
            joinColumns = @JoinColumn(name = "id_biblioteca"),
            inverseJoinColumns = @JoinColumn(name = "id_libro")
    )
    private Set<LibroEntity> librosFavoritos = new HashSet<>();


}

