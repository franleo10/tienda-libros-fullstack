package org.utn.tpfinalprogramacion3.services;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.utn.tpfinalprogramacion3.Exceptions.NoHayUsuariosException;
import org.utn.tpfinalprogramacion3.Exceptions.UsuarioActualmenteDadoDeAltaException;
import org.utn.tpfinalprogramacion3.Exceptions.UsuarioActualmenteDadoDeBajaException;
import org.utn.tpfinalprogramacion3.Exceptions.UsuarioInexistenteException;
import org.utn.tpfinalprogramacion3.dtos.UsuarioAdminDTO;
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

    @PreAuthorize("hasAuthority('ELIMINAR_USUARIOS')")
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

    @PreAuthorize("hasAuthority('ELIMINAR_USUARIOS')")
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

    @PreAuthorize("hasAuthority('ELIMINAR_USUARIOS')")
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

    @PreAuthorize("hasAuthority('ELIMINAR_USUARIOS')")
    public UsuarioCreateDTO findByNombre(String nombre) {

        UsuarioEntity usuarioBuscado = repository.findByNombre(nombre)
                .orElseThrow(
                        () -> new UsuarioInexistenteException("No existe un usuario con ese nombre ne el sistema"));

        UsuarioCreateDTO usuarioDTO = modelMapper.map(usuarioBuscado, UsuarioCreateDTO.class);

        return usuarioDTO;
    }

    @PreAuthorize("hasAuthority('ELIMINAR_USUARIOS')")
    public String delete(int id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("El usuario no existe");
        }

        repository.deleteById(id);
        return "Usuario eliminado con exito";
    }

    @PreAuthorize("hasAuthority('ELIMINAR_USUARIOS')")
    public String verificar_nombre(String nombre) {
        if (repository.existsByNombre(nombre)) {
            return "El usuario existe";
        } else {
            return "El usuario no existe";
        }
    }

    @PreAuthorize("hasAuthority('ELIMINAR_USUARIOS')")
    public String bajaUsuario(int idusuario) {

        if (!repository.existsById(idusuario)) {
            throw new UsuarioInexistenteException("No hay un usuario con el id indicado en el sistema");
        }

        if (idusuario == idUsuarioActual()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No podés darte de baja a vos mismo.");
        }

        UsuarioEntity usuarioBuscado = repository.findById(idusuario).get();

        if (!usuarioBuscado.isActivo()) {
            throw new UsuarioActualmenteDadoDeBajaException("El usuario ya se encuentra dado de baja.");
        }

        usuarioBuscado.setActivo(false);

        repository.save(usuarioBuscado);

        return "Usuario dado de baja existosamente";
    }

    @PreAuthorize("hasAuthority('ELIMINAR_USUARIOS')")
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

    // Tabla del panel de admin: búsqueda por nombre o email, filtro por rol y por estado.
    // A diferencia de findAll, filtra en la base para que la paginación sea correcta.
    @PreAuthorize("hasAuthority('ELIMINAR_USUARIOS')")
    public Page<UsuarioAdminDTO> listarParaAdmin(int numeroPagina, String busqueda, Rol rol, Boolean activo) {
        String texto = (busqueda == null || busqueda.isBlank()) ? null : busqueda.trim();
        Boolean esAdmin = rol == null ? null : rol == Rol.ADMINISTRADOR;

        return repository.buscarParaAdmin(texto, activo, esAdmin, PageRequest.of(numeroPagina, 10))
                .map(this::toAdminDTO);
    }

    private UsuarioAdminDTO toAdminDTO(UsuarioEntity usuario) {
        return new UsuarioAdminDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                rolDe(usuario),
                usuario.isActivo());
    }

    // Si tiene el rol de administrador se muestra ese, si no el de usuario
    private Rol rolDe(UsuarioEntity usuario) {
        CredencialEntity credencial = usuario.getCredencialEntity();
        if (credencial == null || credencial.getRoles() == null) {
            return null;
        }
        boolean esAdmin = credencial.getRoles().stream()
                .anyMatch(r -> r.getRole() == Rol.ADMINISTRADOR);
        return esAdmin ? Rol.ADMINISTRADOR : Rol.USUARIO;
    }

    private int idUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CredencialEntity credencial)
                || credencial.getUsuario() == null) {
            return -1;
        }
        return credencial.getUsuario().getId();
    }
}
