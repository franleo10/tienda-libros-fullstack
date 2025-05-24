package org.utn.tpfinalprogramacion3.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "facturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFactura;

    @Column(name="descripcion", length = 500)
    private String descripcion;

    @Column(name="fecha_compra", nullable = false, length = 100)
    private LocalDate fechaCompra;

    @ManyToOne
    @JoinColumn(name = "id_metodo_pago")
    private MetodoDePagoEntity metodoDePago;

    @ManyToOne
    @JoinColumn(name = "id_carrito")
    private CarritoEntity carrito;

}

