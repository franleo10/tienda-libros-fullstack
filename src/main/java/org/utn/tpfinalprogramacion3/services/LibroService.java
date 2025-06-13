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
import org.utn.tpfinalprogramacion3.Exceptions.GeneroNoEncontrado;
import org.utn.tpfinalprogramacion3.Exceptions.LibroActualmenteDadoDeAltaException;
import org.utn.tpfinalprogramacion3.Exceptions.LibroActualmenteDadoDeBajaException;
import org.utn.tpfinalprogramacion3.Exceptions.LibroInexistenteException;
import org.utn.tpfinalprogramacion3.dtos.LibroDTO;
import org.utn.tpfinalprogramacion3.entities.AutorEntity;
import org.utn.tpfinalprogramacion3.entities.GeneroEntity;
import org.utn.tpfinalprogramacion3.entities.LibroEntity;
import org.utn.tpfinalprogramacion3.mapper.ModelMapperConfig;
import org.utn.tpfinalprogramacion3.repository.AutorRepository;
import org.utn.tpfinalprogramacion3.repository.GeneroRepository;
import org.utn.tpfinalprogramacion3.repository.LibroRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.reactive.function.client.WebClient;

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
                .filter(i -> i.isActivo() == true)
                .map(libro -> modelMapper.map(libro, LibroDTO.class))
                .toList();

        return new PageImpl<>(listaLibrosDTO, pageable, paginaLibros.getTotalElements());
    }

    public Page<LibroDTO> getAllLibrosInactivos(int numeroPagina) {
        int tamañoPagina = 5;  // o el tamaño que quieras fijo
        Pageable pageable = PageRequest.of(numeroPagina, tamañoPagina);

        Page<LibroEntity> paginaLibros = libroRepository.findAll(pageable);

        if (paginaLibros.isEmpty()) {
            throw new RuntimeException("No hay libros disponibles."); // O la excepción que quieras
        }

        List<LibroDTO> listaLibrosDTO = paginaLibros.getContent().stream()
                .filter(i -> i.isActivo() == false)
                .map(libro -> modelMapper.map(libro, LibroDTO.class))
                .toList();

        return new PageImpl<>(listaLibrosDTO, pageable, paginaLibros.getTotalElements());
    }

    public String bajaLogicaLibro(int idLibro){

        if(libroRepository.findById(idLibro).isEmpty()){
            throw new LibroInexistenteException("El libro indicado no existe.");
        }

        LibroEntity libroBuscado = libroRepository.findById(idLibro).get();

        if(!libroBuscado.isActivo()){
            throw new LibroActualmenteDadoDeBajaException("El libro indicado ya se encuentra dado de baja.");
        }

        libroBuscado.setActivo(false);
        libroRepository.save(libroBuscado);

        return "Libro dado de baja correctamente";
    }

    public String altaLogicaLibro(int idLibro){

        if(libroRepository.findById(idLibro).isEmpty()){
            throw new LibroInexistenteException("El libro indicado no existe.");
        }

        LibroEntity libroBuscado = libroRepository.findById(idLibro).get();

        if(libroBuscado.isActivo()){
            throw new LibroActualmenteDadoDeAltaException("El libro indicado ya se encuentra dado de alta.");
        }

        libroBuscado.setActivo(true);
        libroRepository.save(libroBuscado);

        return "Libro dado de alta correctamente";
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
    public LibroDTO crearLibro2(LibroDTO libroDTO, Integer generoId) {
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

            // Buscar género por ID recibido
            GeneroEntity genero = generoRepository.findById(generoId)
                    .orElseThrow(() -> new GeneroNoEncontrado("Género no encontrado con id " + generoId));

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

            // ➤ Obtener sinopsis
            String sinopsis = "Sin sinopsis disponible";
            if (primerDoc.has("key")) {
                String workKey = primerDoc.get("key").asText();
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
                } else {
                    // Si no hay descripción en JSON, probar scraping HTML
                    String workUrlHtml = "https://openlibrary.org" + primerDoc.get("key").asText();
                    String sinopsisHtml = obtenerSinopsisDesdeHtml(workUrlHtml);
                    if (sinopsisHtml != null && !sinopsisHtml.isEmpty()) {
                        sinopsis = sinopsisHtml;
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


    private String obtenerSinopsisDesdeHtml(String workUrl) {
        try {
            String html = WebClient.create()
                    .get()
                    .uri(workUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (html == null || html.isEmpty()) {
                System.out.println("No se obtuvo contenido HTML de " + workUrl);
                return null;
            }

            Document doc = Jsoup.parse(html);

            String[] posiblesSelectores = {
                    "div#description span[itemprop=description]",
                    "div#description",
                    "meta[name=description]",
                    "div.work-description",
                    "section#description" // agrego otro común
            };

            for (String selector : posiblesSelectores) {
                if (selector.startsWith("meta")) {
                    // Para meta tag, obtener contenido del atributo content
                    Elements metaTags = doc.select(selector);
                    for (Element metaTag : metaTags) {
                        String content = metaTag.attr("content");
                        if (content != null && !content.isEmpty()) {
                            System.out.println("Sinopsis encontrada en selector META: " + selector);
                            return content;
                        }
                    }
                } else {
                    Element descElement = doc.selectFirst(selector);
                    if (descElement != null) {
                        String text = descElement.text();
                        if (text != null && !text.isEmpty()) {
                            System.out.println("Sinopsis encontrada en selector: " + selector);
                            return text;
                        }
                    }
                }
            }

            System.out.println("No se encontró sinopsis en el HTML de " + workUrl);
            return null;
        } catch (Exception e) {
            System.err.println("Error al obtener sinopsis desde HTML: " + e.getMessage());
            return null;
        }
    }

    public ResponseEntity<LibroDTO> buscarLibroPorNombre(String titulo) {
        Optional<LibroEntity> libroOpt = libroRepository.findByTitulo(titulo);
        if (libroOpt.isPresent()) {
            LibroDTO dto = modelMapper.map(libroOpt.get(), LibroDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            throw  new LibroInexistenteException("No existe el libro con ese nombre");
        }
    }

    public ResponseEntity<LibroDTO> buscarLibroPorId(int idLibro) {
        Optional<LibroEntity> libroOpt = libroRepository.findById(idLibro);
        if (libroOpt.isPresent()) {
            LibroDTO dto = modelMapper.map(libroOpt.get(), LibroDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            throw new LibroInexistenteException("No existe el libro con ese identificador: " + idLibro);
        }
    }


}