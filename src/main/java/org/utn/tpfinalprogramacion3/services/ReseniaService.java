package org.utn.tpfinalprogramacion3.services;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.Exceptions.DenegarPermisoEliminarReseniaException;
import org.utn.tpfinalprogramacion3.Exceptions.NoHayReseniasException;
import org.utn.tpfinalprogramacion3.Exceptions.ReseniaExistenteException;
import org.utn.tpfinalprogramacion3.dtos.ReseniaCreateDTO;
import org.utn.tpfinalprogramacion3.dtos.ReseniaDTO;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.entities.ReseniaEntity;
import org.utn.tpfinalprogramacion3.repository.LibroRepository;
import org.utn.tpfinalprogramacion3.repository.ReseniaRepository;
import org.utn.tpfinalprogramacion3.repository.UsuarioRepository;

import java.lang.reflect.Type;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ReseniaService {
    private final LibroRepository libroRepository;
    private final ReseniaRepository reseniaRepository;
    private final UsuarioRepository usuarioRepository;
    private ModelMapper modelMapper;

    public ReseniaService(ReseniaRepository repository, ModelMapper modelMapper, LibroRepository libroRepository, ReseniaRepository reseniaRepository, UsuarioRepository usuarioRepository) {
        this.modelMapper = modelMapper;
        this.libroRepository = libroRepository;
        this.reseniaRepository = reseniaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public ReseniaDTO crearResenia(ReseniaCreateDTO dto) {

        try {

            Optional<LibroEntity> libroBuscado = libroRepository.findById(dto.getLibro().getIdLibro());
            if(libroBuscado.isEmpty()){
                throw new NoSuchElementException("El ID del libro ingresado no existe");
            }

            if(reseniaRepository.existsByLibroIdAndNombre(libroBuscado.get().getIdLibro(), dto.getUsuario().getId())){
                throw new ReseniaExistenteException("Ya asignaste una resenia a ese libro");
            }

            dto.getLibro().setIdLibro(libroBuscado.get().getIdLibro());
            dto.getLibro().setTitulo(libroBuscado.get().getTitulo());

            ReseniaEntity reseniaEntity=modelMapper.map(dto, ReseniaEntity.class);

            ReseniaEntity reseniaGuardada=reseniaRepository.save(reseniaEntity);

            return modelMapper.map(reseniaGuardada, ReseniaDTO.class);

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Error al crear el resenia");
        }
    }

    public List<ReseniaDTO> listarResenias() {
        List<ReseniaEntity> resenias = reseniaRepository.findAll();

        if(resenias.isEmpty()){
            throw new NoHayReseniasException("No se encontraron resenias en el sistema");
        }

        Type listType = new TypeToken<List<ReseniaDTO>>() {}.getType();
        return modelMapper.map(reseniaRepository.findAll(), listType);
    }

    public List<ReseniaDTO> listarReseniasByLibro(int idLibro) {

        if(libroRepository.findById(idLibro).isEmpty()){
            throw new NoSuchElementException("El libro no existe");
        }

        List<ReseniaEntity> listaResenias = reseniaRepository.findByLibroId(idLibro);

        if(listaResenias.isEmpty()){
            throw new NoHayReseniasException("El libro seleccionado no tiene resenias");
        }

        Type listType = new TypeToken<List<ReseniaDTO>>() {}.getType();
        return modelMapper.map(listaResenias, listType);
    }

    public List<ReseniaDTO> listarReseniasByUsuario(int idUsuario) {
        if(usuarioRepository.findById(idUsuario).isEmpty()){
            throw new NoSuchElementException("El usuario no existe");
        }

        List<ReseniaEntity> listaResenias = reseniaRepository.findByUsuarioId(idUsuario);

        if(listaResenias.isEmpty()){
            throw new NoHayReseniasException("El usuario no tiene resenias realizadas");
        }

        Type listType = new TypeToken<List<ReseniaDTO>>() {}.getType();
        return modelMapper.map(listaResenias, listType);
    }

    public void eliminarResenia(int idResenia, int idUsuario) {

        if(reseniaRepository.findById(idResenia).isEmpty()){
            throw new ReseniaExistenteException("La resenia no existe");
        }

        if(reseniaRepository.findByReseniaIdAndIdUsuario(idResenia, idUsuario)){
            throw new DenegarPermisoEliminarReseniaException("No puedes eliminar una resenia que no sea tuya");
        }

        reseniaRepository.deleteById(idResenia);
    }
}
