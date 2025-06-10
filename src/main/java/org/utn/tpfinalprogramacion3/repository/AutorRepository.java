package org.utn.tpfinalprogramacion3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.utn.tpfinalprogramacion3.entities.AutorEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<AutorEntity, Integer> {

    public List<AutorEntity> findAllByNombre(String nombre);
    public Optional<AutorEntity> findByidAutor(int id);

    public List<AutorEntity> findAll();
    public void deleteByidAutor(int id);
    Optional<AutorEntity> findByNombreAndApellido(String nombre, String apellido);
    Optional<AutorEntity> findByNombre(String nombre);
}
