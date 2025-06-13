package org.utn.tpfinalprogramacion3.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "resenias")
public class ReseniaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idResenia;

    @Column(name = "descripcion", nullable = false, length = 1000)
    private String texto;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "5.0", inclusive = true)
    @Column(name = "calificacion", nullable = false, precision = 2, scale = 1)
    private BigDecimal calificacion;

    @Column(name = "fecha",nullable = false)
    private LocalDate fecha;

    @PrePersist
    public void asignarFecha() {
        this.fecha = LocalDate.now();
    }

    @ManyToOne
    @JoinColumn(name = "id_usuario",nullable = false)
    private UsuarioEntity usuario;

    @ManyToOne
    @JoinColumn(name = "id_libro",nullable = false)
    @JsonBackReference
    private LibroEntity libro;


}
