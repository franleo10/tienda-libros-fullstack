package org.utn.tpfinalprogramacion3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.utn.tpfinalprogramacion3.entities.GeneroEntity;

import java.util.Optional;

public interface GeneroRepository extends JpaRepository<GeneroEntity,Integer> {

    Optional<GeneroEntity> findByNombre(String nombre);
}
