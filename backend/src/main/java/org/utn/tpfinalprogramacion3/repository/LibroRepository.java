package org.utn.tpfinalprogramacion3.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<LibroEntity, Integer> {

    Optional<LibroEntity> findByTitulo(String titulo);

    // Filtran por activo en la consulta, así las páginas y el total cuentan solo esos libros
    Page<LibroEntity> findAllByActivoTrue(Pageable pageable);
    Page<LibroEntity> findAllByActivoFalse(Pageable pageable);

}
