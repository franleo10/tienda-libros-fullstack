package org.utn.tpfinalprogramacion3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.utn.tpfinalprogramacion3.entities.CarritoEntity;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<CarritoEntity, Integer> {
    Optional<CarritoEntity> findByUsuarioId(Integer idUsuario);

}
