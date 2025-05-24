package org.utn.tpfinalprogramacion3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.utn.tpfinalprogramacion3.entities.ReseniaEntity;

import java.util.List;
import java.util.Optional;

public interface ReseniaRepository extends JpaRepository<ReseniaEntity, Integer> {

    @Query("SELECT r FROM ReseniaEntity r WHERE r.libro.idLibro = :idLibro")
    List<ReseniaEntity> findByLibroId(@Param("idLibro") int idLibro);

    @Query("SELECT COUNT(r) > 0 FROM ReseniaEntity r WHERE r.libro.idLibro = :idLibro AND r.usuario.id = :idUsuario")
    Boolean existsByLibroIdAndNombre(@Param("idLibro") int idLibro, @Param("idUsuario") int idUsuario);

    @Query("SELECT r FROM ReseniaEntity r WHERE r.usuario.id = :idUsuario")
    List<ReseniaEntity> findByUsuarioId(@Param("idUsuario") int idUsuario);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ReseniaEntity r WHERE r.idResenia = :idResenia AND r.usuario.id = :idUsuario")
    Boolean findByReseniaIdAndIdUsuario(@Param("idResenia") int idResenia, @Param("idUsuario") int idUsuario);
}
