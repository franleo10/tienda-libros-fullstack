package org.utn.tpfinalprogramacion3.services;


import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.dtos.UsuarioCreateDTO;
import org.utn.tpfinalprogramacion3.entities.BibliotecaEntity;
import org.utn.tpfinalprogramacion3.entities.CarritoEntity;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.enums.Rol;
import org.utn.tpfinalprogramacion3.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

private final UsuarioRepository repository;
private final ModelMapper modelMapper;

@Autowired
public UsuarioService(UsuarioRepository repository, ModelMapper modelMapper) {
    this.repository = repository;
    this.modelMapper = modelMapper;
}
    @Transactional
    public UsuarioCreateDTO createUsuario(UsuarioCreateDTO dto) {
        try {

            UsuarioEntity usuarioEntity = modelMapper.map(dto, UsuarioEntity.class);
            usuarioEntity.setRoles(Rol.USUARIO);


            CarritoEntity carrito = CarritoEntity.builder()
                    .precio(0.0)
                    .usuario(usuarioEntity)
                    .build();

            usuarioEntity.setCarrito(carrito);



            BibliotecaEntity bibliotecaEntity= BibliotecaEntity.builder()
                    .usuario(usuarioEntity)
                    .libros(null)
                    .build();
            usuarioEntity.setBiblioteca(bibliotecaEntity);

            UsuarioEntity usuarioGuardado = repository.save(usuarioEntity);

            return modelMapper.map(usuarioGuardado, UsuarioCreateDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear el usuario: " + e.getMessage());
        }
    }

    public List<UsuarioEntity> findAll() {
    return repository.findAll();
    }

    public Optional<UsuarioEntity> findByNombre(String nombre){
    return repository.findByNombre(nombre);
    }

    public void delete(int id){
    if (repository.existsById(id)) {
        repository.deleteById(id);
    }else {
        throw new RuntimeException("El usuario no existe");
    }
    }

    public String verificar_nombre(String nombre){
    if (repository.existsByNombre(nombre)) {
        return"El usuario existe";
    }
    else {
        return "El usuario no existe";
    }
    }



}
