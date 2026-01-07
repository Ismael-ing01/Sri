package com.backend.sri.service;

import com.backend.sri.dto.CategoriaDTO;
import com.backend.sri.exception.BusinessRuleException;
import com.backend.sri.exception.ResourceNotFoundException;
import com.backend.sri.model.Categoria;
import com.backend.sri.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)

    public List<CategoriaDTO.Response> findAll() {
        return categoriaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CategoriaDTO.Response findById(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
        return mapToResponse(categoria);
    }

    @Transactional

    public CategoriaDTO.Response create(CategoriaDTO.Request request) {
        if (categoriaRepository.existsByNombre(request.getNombre())) {
            throw new BusinessRuleException("Ya existe una categoría con el nombre: " + request.getNombre());
        }

        Categoria categoria = Categoria.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .activo(true)
                .build();

        return mapToResponse(categoriaRepository.save(categoria));
    }

    @Transactional

    public CategoriaDTO.Response update(Long id, CategoriaDTO.Request request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        if (!categoria.getNombre().equals(request.getNombre()) &&
                categoriaRepository.existsByNombre(request.getNombre())) {
            throw new BusinessRuleException(
                    "El nombre '" + request.getNombre() + "' ya está en uso por otra categoría");
        }

        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());

        return mapToResponse(categoriaRepository.save(categoria));
    }

    @Transactional

    public void delete(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + id);
        }
        // TODO: Validar si tiene productos asociados antes de eliminar (Integridad
        // Referencial)
        categoriaRepository.deleteById(id);
    }

    private CategoriaDTO.Response mapToResponse(Categoria c) {
        return CategoriaDTO.Response.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .descripcion(c.getDescripcion())
                .activo(c.getActivo())
                .build();
    }
}
