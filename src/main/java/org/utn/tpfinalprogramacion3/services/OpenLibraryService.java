package org.utn.tpfinalprogramacion3.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.utn.tpfinalprogramacion3.openLibrary.OpenLibrarySearchResponse;
import reactor.core.publisher.Mono;

@Service
public class OpenLibraryService {
private final WebClient webClient;


    public OpenLibraryService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://openlibrary.org").build();
    }

    /**
     * Busca el libro por título y devuelve el URL directo al PDF si está disponible.
     * @param titulo título del libro
     * @return Mono<String> con la URL del PDF o null si no se encuentra
     */
    public Mono<String> obtenerUrlLibroPorTitulo(String titulo) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("openlibrary.org")
                        .path("/search.json")
                        .queryParam("title", titulo)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> {
                    JsonNode docs = json.get("docs");
                    if (docs != null && docs.isArray() && docs.size() > 0) {
                        JsonNode primeraCoincidencia = docs.get(0);
                        if (primeraCoincidencia.has("key")) {
                            String key = primeraCoincidencia.get("key").asText(); // Ej: "/works/OL12345W"
                            return "https://openlibrary.org" + key;
                        }
                    }
                    return "";
                })
                .onErrorResume(e -> {
                    System.err.println("Error al obtener el link del libro: " + e.getMessage());
                    return Mono.just("");
                });
    }


}
