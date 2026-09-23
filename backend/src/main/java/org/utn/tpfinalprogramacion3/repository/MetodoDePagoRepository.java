package org.utn.tpfinalprogramacion3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.utn.tpfinalprogramacion3.entities.MetodoDePagoEntity;

public interface MetodoDePagoRepository extends JpaRepository<MetodoDePagoEntity, Integer> {
}
