package org.utn.tpfinalprogramacion3.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "resenias")
public class ReseñaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idResenia;

    @Column(name = "descripcion", nullable = false, length = 1000)
    private String texto;
}
