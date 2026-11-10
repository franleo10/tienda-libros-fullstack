package org.utn.tpfinalprogramacion3.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<LibroEntity, Integer> {

    Optional<LibroEntity> findByTitulo(String titulo);

    // Filtran por activo en la consulta, así las páginas y el total cuentan solo esos libros
    Page<LibroEntity> findAllByActivoTrue(Pageable pageable);
    Page<LibroEntity> findAllByActivoFalse(Pageable pageable);

    // Busca entre los libros activos por parte del título o del nombre completo de algún autor.
    // El patrón ya viene armado (por ejemplo "%borges%"); con "%%" devuelve todos.
    @Query("""
            SELECT l FROM LibroEntity l
            WHERE l.activo = true
              AND (LOWER(l.titulo) LIKE :patron
                   OR EXISTS (SELECT a FROM l.autores a
                              WHERE LOWER(CONCAT(a.nombre, ' ', a.apellido)) LIKE :patron))
            """)
    Page<LibroEntity> buscarActivos(@Param("patron") String patron, Pageable pageable);

}
