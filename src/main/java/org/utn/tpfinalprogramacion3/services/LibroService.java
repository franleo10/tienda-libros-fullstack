package org.utn.tpfinalprogramacion3.services;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.reactive.function.client.WebClient;
import org.utn.tpfinalprogramacion3.dtos.LibroDTO;
import org.utn.tpfinalprogramacion3.entities.AutorEntity;
import org.utn.tpfinalprogramacion3.entities.GeneroEntity;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.mapper.ModelMapperConfig;
import org.utn.tpfinalprogramacion3.repository.AutorRepository;
import org.utn.tpfinalprogramacion3.repository.GeneroRepository;
import org.utn.tpfinalprogramacion3.repository.LibroRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final GeneroRepository generoRepository;
    private final ModelMapper modelMapper;
    private final OpenLibraryService openLibraryService;

    @Autowired
    public LibroService(LibroRepository libroRepository, ModelMapper modelMapper, AutorRepository autorRepository, GeneroRepository generoRepository, OpenLibraryService openLibraryService) {
        this.libroRepository = libroRepository;
        this.modelMapper = modelMapper;
        this.autorRepository = autorRepository;
        this.generoRepository = generoRepository;
        this.openLibraryService = openLibraryService;
    }

    @Transactional
    public Optional<LibroDTO> crearLibro(LibroDTO libroDTO, int autor_id, int genero_id) {
        try {
            LibroEntity libro = modelMapper.map(libroDTO, LibroEntity.class);

            Optional<AutorEntity> autorOpt = autorRepository.findByidAutor(autor_id);
            if (autorOpt.isEmpty()) {
                return Optional.empty();
            }

            Optional<GeneroEntity> generoOpt = generoRepository.findById(genero_id);
            if (generoOpt.isEmpty()) {
                return Optional.empty();
            }

            AutorEntity autor = autorOpt.get();
            GeneroEntity genero = generoOpt.get();

            libro.getAutores().add(autor);
            libro.getGeneros().add(genero);


            libro = libroRepository.save(libro);


            return Optional.of(modelMapper.map(libro, LibroDTO.class));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear el libro");
        }
    }


    public Page<LibroDTO> getAllLibros(int numeroPagina) {
        int tamañoPagina = 5;  // o el tamaño que quieras fijo
        Pageable pageable = PageRequest.of(numeroPagina, tamañoPagina);

        Page<LibroEntity> paginaLibros = libroRepository.findAll(pageable);

        if (paginaLibros.isEmpty()) {
            throw new RuntimeException("No hay libros disponibles."); // O la excepción que quieras
        }

        List<LibroDTO> listaLibrosDTO = paginaLibros.getContent().stream()
                .map(libro -> modelMapper.map(libro, LibroDTO.class))
                .toList();

        return new PageImpl<>(listaLibrosDTO, pageable, paginaLibros.getTotalElements());
    }


    public String agregarGeneroALibro(int idLibro, int idGenero) {
        LibroEntity libro = libroRepository.findById(idLibro)
                .orElseThrow(() -> new EntityNotFoundException("Libro no encontrado con id: " + idLibro));

        GeneroEntity genero = generoRepository.findById(idGenero)
                .orElseThrow(() -> new EntityNotFoundException("Género no encontrado con id: " + idGenero));

        if (!libro.getGeneros().contains(genero)) {
            libro.getGeneros().add(genero);
            genero.getLibros().add(libro);
            libroRepository.save(libro);
            generoRepository.save(genero);
            return "Género agregado correctamente al libro.";
        } else {
            throw new IllegalArgumentException("El género ya existe en el libro");
        }
    }

    public String agregarAutorALibro(int idLibro, int idAutor) {
        LibroEntity libro = libroRepository.findById(idLibro)
                .orElseThrow(() -> new EntityNotFoundException("Libro no encontrado con id: " + idLibro));

        AutorEntity autor = autorRepository.findById(idAutor)
                .orElseThrow(() -> new EntityNotFoundException("Autor no encontrado con id: " + idAutor));

        if (!libro.getAutores().contains(autor)) {
            libro.getAutores().add(autor);
            autor.getLibros().add(libro);
            libroRepository.save(libro);
            autorRepository.save(autor);
            return "Autor agregado correctamente al libro.";
        } else {
            throw new IllegalArgumentException("El autor ya existe en el libro");
        }
    }
    public String eliminarAutorALibro(int idLibro, int idAutor) {
        LibroEntity libro = libroRepository.findById(idLibro)
                .orElseThrow(() -> new EntityNotFoundException("Libro no encontrado con id: " + idLibro));

        AutorEntity autor = autorRepository.findById(idAutor)
                .orElseThrow(() -> new EntityNotFoundException("Autor no encontrado con id: " + idAutor));

        if (libro.getAutores().contains(autor)) {
            libro.getAutores().remove(autor);
            autor.getLibros().remove(libro);
            libroRepository.save(libro);
            autorRepository.save(autor);
            return "Autor eliminado correctamente del libro.";
        } else {
            throw new IllegalArgumentException("Autor no existe en el libro");
        }
    }

    @Transactional
    public LibroDTO crearLibro2(LibroDTO libroDTO) {
        try {
            LibroEntity libro = modelMapper.map(libroDTO, LibroEntity.class);

            JsonNode resultado = openLibraryService.buscarDatosLibroPorTitulo(libroDTO.getTitulo()).block();
            if (resultado == null) {
                throw new RuntimeException("No se encontró información del libro");
            }

            JsonNode docs = resultado.get("docs");
            if (docs == null || !docs.isArray() || docs.size() == 0) {
                throw new RuntimeException("No se encontraron resultados para el título");
            }

            JsonNode primerDoc = docs.get(0);

            String nombreAutorCompleto = null;
            if (primerDoc.has("author_name") && primerDoc.get("author_name").isArray() && primerDoc.get("author_name").size() > 0) {
                nombreAutorCompleto = primerDoc.get("author_name").get(0).asText();
            }

            String nombreGenero = "Sin género";
            if (primerDoc.has("subject") && primerDoc.get("subject").isArray() && primerDoc.get("subject").size() > 0) {
                nombreGenero = primerDoc.get("subject").get(0).asText();
            }

            if (nombreAutorCompleto == null) {
                throw new RuntimeException("Falta dato de autor");
            }

            // Separar nombre y apellido
            String nombre = nombreAutorCompleto;
            String apellido = "";
            if (nombreAutorCompleto.contains(" ")) {
                int ultimoEspacio = nombreAutorCompleto.lastIndexOf(" ");
                nombre = nombreAutorCompleto.substring(0, ultimoEspacio);
                apellido = nombreAutorCompleto.substring(ultimoEspacio + 1);
            }

            // Buscar o crear autor
            String finalNombre = nombre;
            String finalApellido = apellido;
            AutorEntity autor = autorRepository.findByNombre(nombre)
                    .orElseGet(() -> autorRepository.save(new AutorEntity(finalNombre, finalApellido)));

            // Buscar o crear género
            String finalNombreGenero = nombreGenero;
            GeneroEntity genero = generoRepository.findByNombre(nombreGenero)
                    .orElseGet(() -> generoRepository.save(new GeneroEntity(finalNombreGenero)));

            libro.getAutores().clear();
            libro.getAutores().add(autor);
            libro.getGeneros().clear();
            libro.getGeneros().add(genero);

            // Guardar libro
            if (libro.getFecha_lanzamiento()== null) {
                if (primerDoc.has("first_publish_year")) {
                    int year = primerDoc.get("first_publish_year").asInt();
                    libro.setFecha_lanzamiento(LocalDate.of(year, 1, 1));
                } else {
                    libro.setFecha_lanzamiento(LocalDate.now());
                }
            }
            if (libro.getPrecio() == null) {
                int precioAleatorio = 1 + (int)(Math.random() * 100);
                libro.setPrecio((float) precioAleatorio);
            }

            // ➤ NUEVO BLOQUE: obtener sinopsis desde /works/{key}.json
            String sinopsis = "Sin sinopsis disponible";
            if (primerDoc.has("key")) {
                String workKey = primerDoc.get("key").asText(); // ej: "/works/OL82536W"
                String workUrl = "https://openlibrary.org" + workKey + ".json";

                JsonNode workResponse = WebClient.create()
                        .get()
                        .uri(workUrl)
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .block();

                if (workResponse != null && workResponse.has("description")) {
                    JsonNode descriptionNode = workResponse.get("description");
                    if (descriptionNode.isTextual()) {
                        sinopsis = descriptionNode.asText();
                    } else if (descriptionNode.has("value")) {
                        sinopsis = descriptionNode.get("value").asText();
                    }
                }
            }

            libro.setSinopsis(sinopsis);

            libro = libroRepository.save(libro);

            return modelMapper.map(libro, LibroDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear el libro: " + e.getMessage());
        }
    }


}