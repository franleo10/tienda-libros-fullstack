package org.utn.tpfinalprogramacion3.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.utn.tpfinalprogramacion3.entities.FacturaEntity;

import java.util.List;

public interface FacturaRepository extends JpaRepository<FacturaEntity, Integer> {
    List<FacturaEntity> findByUsuarioId(Integer usuarioId);

    Page<FacturaEntity> findByUsuarioId(int usuarioId, Pageable pageable);
}
