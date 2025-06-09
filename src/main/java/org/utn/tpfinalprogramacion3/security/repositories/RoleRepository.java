package org.utn.tpfinalprogramacion3.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.utn.tpfinalprogramacion3.enums.Rol;
import org.utn.tpfinalprogramacion3.security.entities.RoleEntity;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRole(Rol role);
}
