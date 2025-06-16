package org.utn.tpfinalprogramacion3.services;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.Exceptions.NoHayUsuariosException;
import org.utn.tpfinalprogramacion3.Exceptions.UsuarioActualmenteDadoDeAltaException;
import org.utn.tpfinalprogramacion3.Exceptions.UsuarioActualmenteDadoDeBajaException;
import org.utn.tpfinalprogramacion3.Exceptions.UsuarioInexistenteException;
import org.utn.tpfinalprogramacion3.dtos.UsuarioCreateDTO;
import org.utn.tpfinalprogramacion3.entities.BibliotecaEntity;
import org.utn.tpfinalprogramacion3.entities.CarritoEntity;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.enums.Rol;
import org.utn.tpfinalprogramacion3.repository.UsuarioRepository;
import org.utn.tpfinalprogramacion3.security.entities.CredencialEntity;

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

            CarritoEntity carrito = CarritoEntity.builder()
                    .precio(0.0)
                    .usuario(usuarioEntity)
                    .build();

            usuarioEntity.setCarrito(carrito);

            BibliotecaEntity bibliotecaEntity = BibliotecaEntity.builder()
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

    public Page<UsuarioCreateDTO> findAll(int numeroPaginacion) {

        Pageable pageable = PageRequest.of(numeroPaginacion, 5);
        Page<UsuarioEntity> paginaUsusarios = repository.findAll(pageable);

        if (paginaUsusarios.isEmpty()) {
            throw new NoHayUsuariosException("No hay usuarios cargados en el sistema.");
        }

        List<UsuarioCreateDTO> listaUsuariosDTO = paginaUsusarios.getContent().stream()
                .filter(i -> i.isActivo() == true)
                .map(i -> modelMapper.map(i, UsuarioCreateDTO.class))
                .toList();

        return new PageImpl<>(listaUsuariosDTO, pageable, paginaUsusarios.getTotalElements());
    }

    public Page<UsuarioCreateDTO> findAllInactivos(int numeroPaginacion) {

        Pageable pageable = PageRequest.of(numeroPaginacion, 5);
        Page<UsuarioEntity> paginaUsusarios = repository.findAll(pageable);

        if (paginaUsusarios.isEmpty()) {
            throw new NoHayUsuariosException("No hay usuarios cargados en el sistema.");
        }

        List<UsuarioCreateDTO> listaUsuariosDTO = paginaUsusarios.getContent().stream()
                .filter(i -> i.isActivo() == false)
                .map(i -> modelMapper.map(i, UsuarioCreateDTO.class))
                .toList();

        return new PageImpl<>(listaUsuariosDTO, pageable, paginaUsusarios.getTotalElements());
    }

    public UsuarioCreateDTO findByNombre(String nombre) {

        UsuarioEntity usuarioBuscado = repository.findByNombre(nombre)
                .orElseThrow(
                        () -> new UsuarioInexistenteException("No existe un usuario con ese nombre ne el sistema"));

        UsuarioCreateDTO usuarioDTO = modelMapper.map(usuarioBuscado, UsuarioCreateDTO.class);

        return usuarioDTO;
    }

    public String delete(int id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("El usuario no existe");
        }

        repository.deleteById(id);
        return "Usuario eliminado con exito";
    }

    public String verificar_nombre(String nombre) {
        if (repository.existsByNombre(nombre)) {
            return "El usuario existe";
        } else {
            return "El usuario no existe";
        }
    }

    public String bajaUsuario(int idusuario) {

        if (!repository.existsById(idusuario)) {
            throw new UsuarioInexistenteException("No hay un usuario con el id indicado en el sistema");
        }

        UsuarioEntity usuarioBuscado = repository.findById(idusuario).get();

        if (!usuarioBuscado.isActivo()) {
            throw new UsuarioActualmenteDadoDeBajaException("El usuario ya se encuentra dado de baja.");
        }

        usuarioBuscado.setActivo(false);

        repository.save(usuarioBuscado);

        return "Usuario dado de baja existosamente";
    }

    public String altaUsuario(int idusuario) {

        if (!repository.existsById(idusuario)) {
            throw new UsuarioInexistenteException("No hay un usuario con el id indicado en el sistema");
        }

        UsuarioEntity usuarioBuscado = repository.findById(idusuario).get();

        if (usuarioBuscado.isActivo()) {
            throw new UsuarioActualmenteDadoDeAltaException("El usuario ya se encuentra dado de alta.");
        }

        usuarioBuscado.setActivo(true);

        repository.save(usuarioBuscado);

        return "Usuario dado de alta existosamente";
    }
}
