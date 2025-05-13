package org.utn.tpfinalprogramacion3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {

public Optional<UsuarioEntity> findByEmail(String email);
public Optional<UsuarioEntity> findByNombre(String nombre);



    @Override
    Optional<UsuarioEntity> findById(Integer integer);

}

