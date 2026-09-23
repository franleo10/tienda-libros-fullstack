package org.utn.tpfinalprogramacion3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.utn.tpfinalprogramacion3.entities.BibliotecaEntity;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;

import java.util.Optional;

public interface BibliotecaRepository extends JpaRepository<BibliotecaEntity, Integer> {
    Optional<BibliotecaEntity> findByUsuario(UsuarioEntity usuario);
    Optional<BibliotecaEntity> findByUsuarioId(int idUsuario);
}
