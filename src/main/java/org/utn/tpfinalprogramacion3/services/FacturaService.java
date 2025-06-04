package org.utn.tpfinalprogramacion3.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.Exceptions.NoHayFacturasException;
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
    private final ModelMapper modelMapper;

    public void generarFactura(Double monto, String externalReference, String metodoPago, Integer idUsuario,
            List<String> titulosLibros) {
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

    public Page<FacturaResponseDTO> obtenerFacturasPorUsuario(int idUsuario, int numeroPaginacion) {

        Pageable pageable = PageRequest.of(numeroPaginacion, 5);
        Page<FacturaEntity> paginaFacturas = facturaRepository.findByUsuarioId(idUsuario, pageable);

        if (paginaFacturas.isEmpty()) {
            throw new NoHayFacturasException("El usuario no tiene facturas.");
        }

        List<FacturaResponseDTO> listaFacturasDTO = paginaFacturas.getContent().stream()
                .map(i -> modelMapper.map(i, FacturaResponseDTO.class))
                .toList();

        return new PageImpl<>(listaFacturasDTO, pageable, paginaFacturas.getTotalElements());
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
