package org.utn.tpfinalprogramacion3.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.Exceptions.*;
import org.utn.tpfinalprogramacion3.dtos.ReseniaCreateDTO;
import org.utn.tpfinalprogramacion3.dtos.ReseniaDTO;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.entities.ReseniaEntity;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.repository.LibroRepository;
import org.utn.tpfinalprogramacion3.repository.ReseniaRepository;
import org.utn.tpfinalprogramacion3.repository.UsuarioRepository;

import java.lang.reflect.Type;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReseniaService {
    private final LibroRepository libroRepository;
    private final ReseniaRepository reseniaRepository;
    private final UsuarioRepository usuarioRepository;
    private ModelMapper modelMapper;

    public ReseniaService(ReseniaRepository repository, ModelMapper modelMapper, LibroRepository libroRepository,
            ReseniaRepository reseniaRepository, UsuarioRepository usuarioRepository) {
        this.modelMapper = modelMapper;
        this.libroRepository = libroRepository;
        this.reseniaRepository = reseniaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public ReseniaDTO crearResenia(ReseniaCreateDTO dto, int idLibro, int idUsuario) {

        try {

            Optional<LibroEntity> libroBuscado = libroRepository.findById(idLibro);
            if (libroBuscado.isEmpty()) {
                throw new LibroInexistenteException("No existe un libro con el id " + idLibro + " asignado");
            }

            Optional<UsuarioEntity> usuarioBuscado = usuarioRepository.findById(idUsuario);

            if (usuarioBuscado.isEmpty()) {
                throw new UsuarioInexistenteException("No existe un usuario con el id " + idUsuario + " asignado");
            }

            if (reseniaRepository.existsByLibroIdAndUsuarioId(libroBuscado.get().getIdLibro(),
                    usuarioBuscado.get().getId())) {
                throw new ReseniaExistenteException("Ya asignaste una resenia a ese libro");
            }

            ReseniaEntity resenia = ReseniaEntity.builder()
                    .calificacion(dto.getCalificacion())
                    .usuario(usuarioBuscado.get())
                    .texto(dto.getTexto())
                    .libro(libroBuscado.get())
                    .build();

            ReseniaEntity reseniaGuardada = reseniaRepository.save(resenia);

            return modelMapper.map(reseniaGuardada, ReseniaDTO.class);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear el resenia");
        }
    }

    public Page<ReseniaDTO> listarResenias(int numeroPaginacion) {

        Pageable pageable = PageRequest.of(numeroPaginacion, 5);
        Page<ReseniaEntity> paginaResenias = reseniaRepository.findAll(pageable);

        if (paginaResenias.isEmpty()) {
            throw new NoHayReseniasException("No se encontraron resenias en el sistema");
        }

        List<ReseniaDTO> listaReseniasDTO = paginaResenias.getContent().stream()
                .map(i -> modelMapper.map(i, ReseniaDTO.class))
                .toList();

        return new PageImpl<>(listaReseniasDTO, pageable, paginaResenias.getTotalElements());
    }

    public Page<ReseniaDTO> listarReseniasByLibro(int idLibro, int numeroPaginacion) {

        if (libroRepository.findById(idLibro).isEmpty()) {
            throw new NoSuchElementException("El libro no existe");
        }

        Pageable pageable = PageRequest.of(numeroPaginacion, 5);
        Page<ReseniaEntity> paginaResenias = reseniaRepository.findByLibroId(idLibro, pageable);

        if (paginaResenias.isEmpty()) {
            throw new NoHayReseniasException("El libro seleccionado no tiene resenias");
        }

        List<ReseniaDTO> listaResenias = paginaResenias.getContent().stream()
                .map(i -> modelMapper.map(i, ReseniaDTO.class))
                .toList();

        return new PageImpl<>(listaResenias, pageable, paginaResenias.getTotalElements());
    }

    public Page<ReseniaDTO> listarReseniasByUsuario(int idUsuario, int numeroPaginacion) {

        if (usuarioRepository.findById(idUsuario).isEmpty()) {
            throw new NoSuchElementException("El usuario no existe");
        }

        Pageable pageable = PageRequest.of(numeroPaginacion, 5);
        Page<ReseniaEntity> paginaResenias = reseniaRepository.findByUsuarioId(idUsuario, pageable);

        if (paginaResenias.isEmpty()) {
            throw new NoHayReseniasException("El usuario no tiene resenias realizadas");
        }

        List<ReseniaDTO> listaResenias = paginaResenias.getContent().stream()
                .map(i -> modelMapper.map(i, ReseniaDTO.class))
                .toList();

        return new PageImpl<>(listaResenias, pageable, paginaResenias.getTotalElements());
    }

    public void eliminarResenia(int idResenia, int idUsuario) {

        if (reseniaRepository.findById(idResenia).isEmpty()) {
            throw new ReseniaExistenteException("La resenia no existe");
        }

        if (reseniaRepository.findByReseniaIdAndIdUsuario(idResenia, idUsuario)) {
            throw new DenegarPermisoEliminarReseniaException("No puedes eliminar una resenia que no sea tuya");
        }

        reseniaRepository.deleteById(idResenia);
    }
}
