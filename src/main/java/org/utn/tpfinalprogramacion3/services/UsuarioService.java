package org.utn.tpfinalprogramacion3.services;


import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.dtos.UsuarioCreateDTO;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.repository.UsuarioRepository;

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
            UsuarioEntity usuarioEntity1 = repository.save(usuarioEntity);
            return modelMapper.map(usuarioEntity1, UsuarioCreateDTO.class);
        } catch (Exception e) {
            e.printStackTrace(); // Esto imprimirá la excepción en la consola
            throw new RuntimeException("Error al crear el usuario: " + e.getMessage());
        }
    }



}
