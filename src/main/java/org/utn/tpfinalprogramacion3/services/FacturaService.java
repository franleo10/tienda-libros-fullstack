package org.utn.tpfinalprogramacion3.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.dtos.FacturaDTO;
import org.utn.tpfinalprogramacion3.dtos.FacturaResponseDTO;
import org.utn.tpfinalprogramacion3.entities.CarritoEntity;
import org.utn.tpfinalprogramacion3.entities.FacturaEntity;
import org.utn.tpfinalprogramacion3.entities.MetodoDePagoEntity;
import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;
import org.utn.tpfinalprogramacion3.repository.CarritoRepository;
import org.utn.tpfinalprogramacion3.repository.FacturaRepository;
import org.utn.tpfinalprogramacion3.repository.MetodoDePagoRepository;
import org.utn.tpfinalprogramacion3.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarritoRepository carritoRepository;

    public void generarFactura(Double monto, String externalReference, String metodoPago, Integer idUsuario, List<String> titulosLibros) {
        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUsuario));


        String librosStr = String.join(", ", titulosLibros);

        FacturaEntity factura = FacturaEntity.builder()
                .monto(monto)
                .fechaCompra(LocalDateTime.now())
                .metodoPago(metodoPago)
                .externalReference(externalReference)
                .usuario(usuario)
                .librosComprados(librosStr)
                .build();

        facturaRepository.save(factura);
    }

    public List<FacturaResponseDTO> obtenerFacturasPorUsuario(int idUsuario) {
        return facturaRepository.findByUsuarioId(idUsuario)
                .stream()
                .map(this::mapFacturaToDTO)
                .toList();
    }

    public FacturaResponseDTO mapFacturaToDTO(FacturaEntity factura) {
        Integer idUsuario = factura.getUsuario().getId();

        List<String> titulosLibrosComprados = List.of();

        if (factura.getLibrosComprados() != null && !factura.getLibrosComprados().isEmpty()) {
            titulosLibrosComprados = Arrays.asList(factura.getLibrosComprados().split(", "));
        }

        return FacturaResponseDTO.builder()
                .idFactura(factura.getIdFactura())
                .monto(factura.getMonto())
                .fechaCompra(factura.getFechaCompra())
                .metodoPago(factura.getMetodoPago())
                .externalReference(factura.getExternalReference())
                .idUsuario(idUsuario)
                .titulosLibrosComprados(titulosLibrosComprados)
                .build();
    }





}
