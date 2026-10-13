package org.utn.tpfinalprogramacion3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.utn.tpfinalprogramacion3.entities.AutorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<AutorEntity, Integer> {

    public List<AutorEntity> findAllByNombre(String nombre);
    public Page<AutorEntity> findAllByNombre(String nombre, Pageable pageable);
    public Optional<AutorEntity> findByidAutor(int id);

    public List<AutorEntity> findAll();
    public void deleteByidAutor(int id);
    Optional<AutorEntity> findByNombreAndApellido(String nombre, String apellido);
    Optional<AutorEntity> findByNombre(String nombre);
    Optional<AutorEntity> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndApellidoIgnoreCase(String nombre, String apellido);
    boolean existsByNombreIgnoreCaseAndApellidoIgnoreCaseAndIdAutorNot(
            String nombre,
            String apellido,
            Integer idAutor
    );
    Optional<AutorEntity> findByIdAutorAndActivoTrue(Integer idAutor);
    Optional<AutorEntity> findByNombreIgnoreCaseAndActivoTrue(String nombre);
    Optional<AutorEntity> findByNombreAndActivoTrue(String nombre);
    Page<AutorEntity> findAllByActivoTrue(Pageable pageable);
    Page<AutorEntity> findAllByActivoFalse(Pageable pageable);
    Page<AutorEntity> findAllByNombreIgnoreCaseContainingAndActivoTrue(String nombre, Pageable pageable);
    Page<AutorEntity> findAllByNombreIgnoreCaseContainingAndActivoFalse(String nombre, Pageable pageable);

    @Query("select a.idAutor, count(l) from AutorEntity a left join a.libros l " +
            "where a.idAutor in :ids group by a.idAutor")
    List<Object[]> contarLibrosPorAutor(@Param("ids") List<Integer> ids);

    @Query("select a from AutorEntity a where a.activo = true and (" +
            "lower(concat(a.nombre, ' ', a.apellido)) like lower(concat('%', :termino, '%')) " +
            "or lower(a.nombre) like lower(concat('%', :termino, '%')) " +
            "or lower(a.apellido) like lower(concat('%', :termino, '%')))")
    Page<AutorEntity> buscarPorNombre(@Param("termino") String termino, Pageable pageable);

    @Query("select a from AutorEntity a where a.activo = false and (" +
            "lower(concat(a.nombre, ' ', a.apellido)) like lower(concat('%', :termino, '%')) " +
            "or lower(a.nombre) like lower(concat('%', :termino, '%')) " +
            "or lower(a.apellido) like lower(concat('%', :termino, '%')))")
    Page<AutorEntity> buscarInactivosPorNombre(@Param("termino") String termino, Pageable pageable);
}
