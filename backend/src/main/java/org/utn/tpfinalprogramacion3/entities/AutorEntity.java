package org.utn.tpfinalprogramacion3.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "autores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AutorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAutor;
    @Column(name="nombre", nullable = false, length = 100)
    private String nombre;
    @Column(name="apellido", nullable = false, length = 100)
    private String apellido;


    @ManyToMany(mappedBy = "autores")
    @JsonBackReference
    private List<LibroEntity> libros;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutorEntity that = (AutorEntity) o;
        return idAutor == that.idAutor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAutor);
    }

    public AutorEntity(String nombre, String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
    }
}

