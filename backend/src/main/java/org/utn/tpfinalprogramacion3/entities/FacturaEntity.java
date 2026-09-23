package org.utn.tpfinalprogramacion3.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Column(name = "monto", nullable = false)
    private Double monto;

    @Column(name="fecha_compra", nullable = false, length = 100)
    private LocalDateTime fechaCompra;

    @Column(name = "metodo_pago", nullable = false, length = 50)
    private String metodoPago;

    @Column(name = "external_reference", nullable = false, length = 100)
    private String externalReference;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioEntity usuario;

    @Column(name = "libros_comprados", length = 1000)
    private String librosComprados;

}

