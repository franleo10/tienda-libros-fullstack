package org.utn.tpfinalprogramacion3.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {

public Optional<UsuarioEntity> findByEmail(String email);
public Optional<UsuarioEntity> findByNombre(String nombre);

    void deleteById(Integer id);

    public List<UsuarioEntity>findAll();

    @Override
    Optional<UsuarioEntity> findById(Integer integer);


    boolean existsByEmail(String email);
    boolean existsByNombre(String nombre);

    // Búsqueda del panel de admin: cada filtro se ignora cuando llega en null.
    // esAdmin = true trae solo administradores, false solo usuarios comunes.
    @Query("""
            SELECT u FROM UsuarioEntity u
            WHERE (:busqueda IS NULL
                   OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%'))
                   OR LOWER(u.email) LIKE LOWER(CONCAT('%', :busqueda, '%')))
              AND (:activo IS NULL OR u.activo = :activo)
              AND (:esAdmin IS NULL
                   OR (:esAdmin = true AND EXISTS (
                        SELECT 1 FROM CredencialEntity c JOIN c.roles r
                        WHERE c.usuario = u AND r.role = org.utn.tpfinalprogramacion3.enums.Rol.ADMINISTRADOR))
                   OR (:esAdmin = false AND NOT EXISTS (
                        SELECT 1 FROM CredencialEntity c JOIN c.roles r
                        WHERE c.usuario = u AND r.role = org.utn.tpfinalprogramacion3.enums.Rol.ADMINISTRADOR)))
            ORDER BY u.id
            """)
    Page<UsuarioEntity> buscarParaAdmin(@Param("busqueda") String busqueda,
                                        @Param("activo") Boolean activo,
                                        @Param("esAdmin") Boolean esAdmin,
                                        Pageable pageable);

}

