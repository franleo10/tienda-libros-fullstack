package org.utn.tpfinalprogramacion3.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bibliotecas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BibliotecaEntity {

    @EmbeddedId
    private BibliotecaId id;

    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

    @ManyToOne
    @MapsId("idLibro")
    @JoinColumn(name = "id_libro")
    private LibroEntity libro;

    @ManyToOne
    @JoinColumn(name = "id_factura")
    private FacturaEntity factura;
}
