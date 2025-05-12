package org.utn.tpfinalprogramacion3.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name="libros")
public class LibroEntity {
    @Id
    private int idLibro;
    @Column(name="titulo", nullable = false, length = 100)
    private String titulo;
}
