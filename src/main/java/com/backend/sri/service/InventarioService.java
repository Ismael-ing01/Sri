package com.backend.sri.service;

import com.backend.sri.dto.InventarioDTO;
import com.backend.sri.exception.BusinessRuleException;
import com.backend.sri.exception.ResourceNotFoundException;
import com.backend.sri.model.Bodega;
import com.backend.sri.model.Inventario;
import com.backend.sri.model.Producto;
import com.backend.sri.repository.BodegaRepository;
import com.backend.sri.repository.InventarioRepository;
import com.backend.sri.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;
    private final BodegaRepository bodegaRepository;

    @Transactional
    public InventarioDTO.Response ajustarStock(InventarioDTO.AdjustmentRequest request) {
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado con ID: " + request.getProductoId()));

        Bodega bodega = bodegaRepository.findById(request.getBodegaId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Bodega no encontrada con ID: " + request.getBodegaId()));

        Inventario inventario = inventarioRepository
                .findByProductoIdAndBodegaId(request.getProductoId(), request.getBodegaId())
                .orElse(Inventario.builder()
                        .producto(producto)
                        .bodega(bodega)
                        .cantidad(0)
                        .build());

        if (request.getEsIngreso()) {
            inventario.setCantidad(inventario.getCantidad() + request.getCantidad());
        } else {
            if (inventario.getCantidad() < request.getCantidad()) {
                throw new BusinessRuleException("Stock insuficiente en la bodega '" + bodega.getNombre() +
                        "'. Disponible: " + inventario.getCantidad());
            }
            inventario.setCantidad(inventario.getCantidad() - request.getCantidad());
        }

        return mapToResponse(inventarioRepository.save(inventario));
    }

    @Transactional(readOnly = true)

    public List<InventarioDTO.Response> findByBodega(Long bodegaId) {
        if (!bodegaRepository.existsById(bodegaId)) {
            throw new ResourceNotFoundException("Bodega no encontrada con ID: " + bodegaId);
        }
        return inventarioRepository.findByBodegaId(bodegaId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)

    public List<InventarioDTO.Response> findByProducto(Long productoId) {
        if (!productoRepository.existsById(productoId)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + productoId);
        }
        return inventarioRepository.findByProductoId(productoId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private InventarioDTO.Response mapToResponse(Inventario i) {
        return InventarioDTO.Response.builder()
                .id(i.getId())
                .productoNombre(i.getProducto().getNombre())
                .productoCodigo(i.getProducto().getCodigoPrincipal())
                .bodegaNombre(i.getBodega().getNombre())
                .cantidadActual(i.getCantidad())
                .build();
    }
}
