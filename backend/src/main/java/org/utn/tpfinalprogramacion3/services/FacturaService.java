package org.utn.tpfinalprogramacion3.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
import org.springframework.security.core.Authentication;
import org.utn.tpfinalprogramacion3.security.entities.CredencialEntity;

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

    public FacturaEntity generarFactura(Double monto, String externalReference, String metodoPago, Integer idUsuario,
            List<String> titulosLibros, Long idPago) {
        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUsuario));

        // Eliminar títulos duplicados
        List<String> titulosUnicos = titulosLibros.stream()
                .distinct()
                .toList();
        String librosStr = String.join(", ", titulosUnicos);

        FacturaEntity factura = FacturaEntity.builder()
                .monto(monto)
                .fechaCompra(LocalDateTime.now())
                .metodoPago(metodoPago)
                .externalReference(externalReference)
                .idPago(idPago)
                .usuario(usuario)
                .librosComprados(librosStr)
                .build();

        return facturaRepository.save(factura);
    }

    // Historial de compras del usuario logueado, de la más reciente a la más antigua.
    // Si no compró nada devuelve una página vacía (la pantalla muestra "todavía no compraste").
    @PreAuthorize("hasAuthority('VER_SU_FACTURA')")
    public Page<FacturaResponseDTO> obtenerFacturasPorUsuario(int numeroPaginacion) {
        Pageable pageable = PageRequest.of(numeroPaginacion, 10,
                Sort.by(Sort.Direction.DESC, "fechaCompra").and(Sort.by(Sort.Direction.DESC, "idFactura")));

        return facturaRepository.findByUsuarioId(idUsuarioActual(), pageable)
                .map(this::mapFacturaToDTO);
    }

    // Detalle de una compra. Si la factura es de otro usuario responde lo mismo que si no existiera.
    @PreAuthorize("hasAuthority('VER_SU_FACTURA')")
    public FacturaResponseDTO obtenerFacturaPropia(int idFactura) {
        return facturaRepository.findById(idFactura)
                .filter(factura -> factura.getUsuario().getId() == idUsuarioActual())
                .map(this::mapFacturaToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No encontramos esa compra."));
    }

    private int idUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CredencialEntity credencial = (CredencialEntity) authentication.getPrincipal();
        return credencial.getUsuario().getId();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Page<FacturaResponseDTO> obtenerFacturasPorUsuarioParaAdmin(int idUsuario, int numeroPaginacion) {

        // Obtener ID del usuario logueado desde el token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CredencialEntity credencial = (CredencialEntity) authentication.getPrincipal();
        Long userId = credencial.getId();

        Pageable pageable = PageRequest.of(numeroPaginacion, 5);
        Page<FacturaEntity> paginaFacturas = facturaRepository.findByUsuarioId(idUsuario, pageable);

        if (paginaFacturas.isEmpty()) {
            throw new NoHayFacturasException("El usuario no tiene facturas.");
        }

        List<FacturaResponseDTO> listaFacturasDTO = paginaFacturas.getContent().stream()
                .map(this::mapFacturaToDTO)
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
                .idPago(factura.getIdPago())
                .idUsuario(idUsuario)
                .titulosLibrosComprados(titulosLibrosComprados)
                .build();
    }

}
