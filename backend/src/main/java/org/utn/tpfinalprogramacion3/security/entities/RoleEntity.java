package org.utn.tpfinalprogramacion3.security.entities;

import jakarta.persistence.*;
import lombok.*;
import org.utn.tpfinalprogramacion3.enums.Rol;
import org.utn.tpfinalprogramacion3.security.enums.Permits;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "roles")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", unique = true, nullable = false)
    private Rol role;

    @ElementCollection(targetClass = Permits.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "role_permits", joinColumns = @JoinColumn(name = "role_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permit")
    private Set<Permits> permits;
}
