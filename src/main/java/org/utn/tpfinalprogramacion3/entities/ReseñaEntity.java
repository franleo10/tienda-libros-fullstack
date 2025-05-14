package org.utn.tpfinalprogramacion3.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.time.LocalDate;

@Entity
@Table(name = "resenias")
public class ReseñaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idResenia;

    @Column(name = "descripcion", nullable = false, length = 1000)
    private String texto;

    @Min(1)
    @Max(5)
    @Column(name = "calificacion", nullable = false)
    private float calificacion;

    @Column(name = "fecha",nullable = false)
    private LocalDate fecha=LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "nombre_usuario",nullable = false)
    private UsuarioEntity usuario;
}
