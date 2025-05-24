package org.utn.tpfinalprogramacion3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.utn.tpfinalprogramacion3.entities.BibliotecaEntity;
import org.utn.tpfinalprogramacion3.entities.BibliotecaId;

public interface BibliotecaRepository extends JpaRepository<BibliotecaEntity, BibliotecaId> {

}
