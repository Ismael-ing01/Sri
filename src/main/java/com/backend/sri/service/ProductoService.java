package com.backend.sri.service;

import com.backend.sri.dto.ProductoDTO;
import com.backend.sri.exception.BusinessRuleException;
import com.backend.sri.exception.ResourceNotFoundException;
import com.backend.sri.model.Categoria;
import com.backend.sri.model.Producto;
import com.backend.sri.repository.CategoriaRepository;
import com.backend.sri.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)

    public List<ProductoDTO.Response> findAll() {
        return productoRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)

    public ProductoDTO.Response findById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return mapToResponse(producto);
    }

    @Transactional

    public ProductoDTO.Response create(ProductoDTO.Request request) {
        if (productoRepository.existsByCodigoPrincipal(request.getCodigoPrincipal())) {
            throw new BusinessRuleException(
                    "Ya existe un producto con el código: " + request.getCodigoPrincipal());
        }

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría no encontrada con ID: " + request.getCategoriaId()));

        Producto producto = Producto.builder()
                .codigoPrincipal(request.getCodigoPrincipal())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precioCompra(request.getPrecioCompra())
                .margenGanancia(request.getMargenGanancia())
                .tieneIva(request.getTieneIva())
                .categoria(categoria)
                .activo(true)
                .build();

        // El @PrePersist de la entidad Producto calculará el precioVenta
        // automáticamente
        return mapToResponse(productoRepository.save(producto));
    }

    @Transactional

    public ProductoDTO.Response update(Long id, ProductoDTO.Request request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        if (!producto.getCodigoPrincipal().equals(request.getCodigoPrincipal()) &&
                productoRepository.existsByCodigoPrincipal(request.getCodigoPrincipal())) {
            throw new BusinessRuleException(
                    "El código '" + request.getCodigoPrincipal() + "' ya está en uso por otro producto");
        }

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría no encontrada con ID: " + request.getCategoriaId()));

        producto.setCodigoPrincipal(request.getCodigoPrincipal());
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecioCompra(request.getPrecioCompra());
        producto.setMargenGanancia(request.getMargenGanancia());
        producto.setTieneIva(request.getTieneIva());
        producto.setCategoria(categoria);

        // El @PreUpdate recalculará el precioVenta si cambiaron costo o margen
        return mapToResponse(productoRepository.save(producto));
    }

    @Transactional

    public void delete(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    private ProductoDTO.Response mapToResponse(Producto p) {
        return ProductoDTO.Response.builder()
                .id(p.getId())
                .codigoPrincipal(p.getCodigoPrincipal())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .precioCompra(p.getPrecioCompra())
                .margenGanancia(p.getMargenGanancia())
                .precioVenta(p.getPrecioVenta())
                .tieneIva(p.getTieneIva())
                .categoriaNombre(p.getCategoria().getNombre())
                .activo(p.getActivo())
                .build();
    }
}
