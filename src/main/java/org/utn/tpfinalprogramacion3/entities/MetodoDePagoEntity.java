package org.utn.tpfinalprogramacion3.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "metodos_de_pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetodoDePagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMp;

    private String nombre;

    @OneToMany(mappedBy = "metodoDePago")
    private List<FacturaEntity> facturas;

    @ManyToOne
    @JoinColumn(name = "id_usuario",nullable = false)
    private UsuarioEntity usuario;

}
