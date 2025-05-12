package org.utn.tpfinalprogramacion3.entities;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "historial_de_compra")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialDeCompraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idHistorial;

    @OneToOne
    @JoinColumn(name = "id_factura")
    private FacturaEntity factura;

    @OneToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;


}
