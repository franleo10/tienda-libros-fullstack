package org.utn.tpfinalprogramacion3.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.utn.tpfinalprogramacion3.enums.Rol;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="usuarios")
@Data

public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;
    @Column (name = "edad", nullable = false)
    private int edad;
    @Column (name="email", nullable = false, length = 100)
    private String email;

    @Column (name="roles", nullable = false, length = 25)
    @Enumerated(EnumType.STRING)
    private Rol roles;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private CredencialEntity credencialEntity;
    @OneToMany(mappedBy = "usuario")
    private List<BibliotecaEntity> biblioteca;
}
