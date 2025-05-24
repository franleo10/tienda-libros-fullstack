package org.utn.tpfinalprogramacion3.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.utn.tpfinalprogramacion3.dtos.FacturaDTO;
import org.utn.tpfinalprogramacion3.entities.CarritoEntity;
import org.utn.tpfinalprogramacion3.entities.FacturaEntity;
import org.utn.tpfinalprogramacion3.entities.MetodoDePagoEntity;
import org.utn.tpfinalprogramacion3.repository.CarritoRepository;
import org.utn.tpfinalprogramacion3.repository.FacturaRepository;
import org.utn.tpfinalprogramacion3.repository.MetodoDePagoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final MetodoDePagoRepository metodoDePagoRepository;
    private final CarritoRepository carritoRepository;
    private final ModelMapper modelMapper;

    public FacturaDTO crearFactura(FacturaDTO dto) {
        FacturaEntity entity = modelMapper.map(dto, FacturaEntity.class);

        entity.setMetodoDePago(obtenerMetodo(dto.getMetodoDePagoId()));
        entity.setCarrito(obtenerCarrito(dto.getCarritoId()));

        return modelMapper.map(facturaRepository.save(entity), FacturaDTO.class);
    }
    public List<FacturaDTO> obtenerFacturasPorUsuario(Integer usuarioId) {
        return facturaRepository.findByCarritoUsuarioId(usuarioId).stream()
                .map(entity -> modelMapper.map(entity, FacturaDTO.class))
                .collect(Collectors.toList());
    }


    public FacturaDTO obtenerFacturaPorId(Integer id) {
        return facturaRepository.findById(id)
                .map(entity -> modelMapper.map(entity, FacturaDTO.class))
                .orElse(null);
    }

    public List<FacturaDTO> obtenerTodas() {
        return facturaRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, FacturaDTO.class))
                .collect(Collectors.toList());
    }

    public FacturaDTO actualizarFactura(Integer id, FacturaDTO dto) {
        Optional<FacturaEntity> optional = facturaRepository.findById(id);
        if (optional.isEmpty()) return null;

        FacturaEntity entity = optional.get();

        modelMapper.map(dto, entity); // actualiza los campos simples
        entity.setMetodoDePago(obtenerMetodo(dto.getMetodoDePagoId()));
        entity.setCarrito(obtenerCarrito(dto.getCarritoId()));

        return modelMapper.map(facturaRepository.save(entity), FacturaDTO.class);
    }

    public void eliminarFactura(Integer id) {
        facturaRepository.deleteById(id);
    }

    private MetodoDePagoEntity obtenerMetodo(Integer id) {
        return id != null ? metodoDePagoRepository.findById(id).orElse(null) : null;
    }

    private CarritoEntity obtenerCarrito(Integer id) {
        return id != null ? carritoRepository.findById(id).orElse(null) : null;
    }
}
