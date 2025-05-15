package org.utn.tpfinalprogramacion3.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table (name="libros")
public class LibroEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idLibro;
    @Column(name="titulo", nullable = false, length = 100)
    private String titulo;
    @Column(name = "sinopsis", nullable = false, length = 100)
    private String sinopsis;
    @Column(name = "fecha_lanzamiento", nullable = false)
    private LocalDate fecha_lanzamiento;
    @Column(name = "precio", nullable = false)
    private Float precio;
    @Column(name = "reseñas", nullable = true, length = 4)

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "autorXlibro",
            joinColumns = @JoinColumn(name = "id_libro"),
            inverseJoinColumns = @JoinColumn(name = "id_autor")
    )
    @JsonManagedReference
    private List<AutorEntity> autores = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "libro_xgenero",
            joinColumns = @JoinColumn(name = "id_libro"),
            inverseJoinColumns = @JoinColumn(name = "id_genero")
    )
    @JsonManagedReference
    private List<GeneroEntity> generos = new ArrayList<>();




}
