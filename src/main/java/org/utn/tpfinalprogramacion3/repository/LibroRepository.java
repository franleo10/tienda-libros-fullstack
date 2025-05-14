package org.utn.tpfinalprogramacion3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<LibroEntity, Integer> {

    public Optional<LibroEntity> findById();
    public List<LibroEntity> findAll();
    public void deleteById();
    public Optional<LibroEntity> findByNombre();

}
