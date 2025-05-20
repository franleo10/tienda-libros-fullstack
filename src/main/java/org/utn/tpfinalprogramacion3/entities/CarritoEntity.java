package org.utn.tpfinalprogramacion3.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCarrito;

    @Column(name ="precio")
    private Double precio;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

    @ManyToMany
    @JoinTable(
            name = "carrito_libro",
            joinColumns = @JoinColumn(name = "id_carrito"),
            inverseJoinColumns = @JoinColumn(name = "id_libro")
    )
    private List<LibroEntity> libros= new ArrayList<>();


    @OneToMany(mappedBy = "carrito")
    private List<FacturaEntity> facturas;
}

