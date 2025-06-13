package org.utn.tpfinalprogramacion3.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table (name="libros")
public class LibroEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int idLibro;
    @Column(name="titulo", nullable = false, length = 100)
    private String titulo;

    @Lob
    @Column(name = "sinopsis", nullable = false,columnDefinition = "TEXT")
    private String sinopsis;

    @Column(name = "fecha_lanzamiento", nullable = false)
    private LocalDate fecha_lanzamiento;
    @Column(name = "precio", nullable = false)
    private Float precio;
    
    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    List<ReseniaEntity> listaResenias = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "autorXlibro",
            joinColumns = @JoinColumn(name = "id_libro"),
            inverseJoinColumns = @JoinColumn(name = "id_autor")
    )
    @JsonManagedReference
    private Set<AutorEntity> autores = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "libro_xgenero",
            joinColumns = @JoinColumn(name = "id_libro"),
            inverseJoinColumns = @JoinColumn(name = "id_genero")
    )
    @JsonManagedReference
    private Set<GeneroEntity> generos = new HashSet<>();
    @Column(name = "url_pdf", length = 500)
    private String urlPdf;
    @Column(name = "activo", nullable = false)
    private boolean activo;


    @PrePersist
    public void actividad(){
        this.activo = true;
    }




}
