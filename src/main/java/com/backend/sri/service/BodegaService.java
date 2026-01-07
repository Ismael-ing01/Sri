package com.backend.sri.service;

import com.backend.sri.dto.BodegaDTO;
import com.backend.sri.exception.BusinessRuleException;
import com.backend.sri.exception.ResourceNotFoundException;
import com.backend.sri.model.Bodega;
import com.backend.sri.repository.BodegaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BodegaService {

    private final BodegaRepository bodegaRepository;

    @Transactional(readOnly = true)

    public List<BodegaDTO.Response> findAll() {
        return bodegaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)

    public BodegaDTO.Response findById(Long id) {
        Bodega bodega = bodegaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con ID: " + id));
        return mapToResponse(bodega);
    }

    @Transactional

    public BodegaDTO.Response create(BodegaDTO.Request request) {
        if (bodegaRepository.existsByNombre(request.getNombre())) {
            throw new BusinessRuleException("Ya existe una bodega con el nombre: " + request.getNombre());
        }

        Bodega bodega = Bodega.builder()
                .nombre(request.getNombre())
                .ubicacion(request.getUbicacion())
                .telefono(request.getTelefono())
                .responsable(request.getResponsable())
                .activo(true)
                .build();

        return mapToResponse(bodegaRepository.save(bodega));
    }

    @Transactional

    public BodegaDTO.Response update(Long id, BodegaDTO.Request request) {
        Bodega bodega = bodegaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con ID: " + id));

        if (!bodega.getNombre().equals(request.getNombre()) &&
                bodegaRepository.existsByNombre(request.getNombre())) {
            throw new BusinessRuleException("El nombre '" + request.getNombre() + "' ya está en uso por otra bodega");
        }

        bodega.setNombre(request.getNombre());
        bodega.setUbicacion(request.getUbicacion());
        bodega.setTelefono(request.getTelefono());
        bodega.setResponsable(request.getResponsable());

        return mapToResponse(bodegaRepository.save(bodega));
    }

    @Transactional

    public void delete(Long id) {
        if (!bodegaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bodega no encontrada con ID: " + id);
        }
        bodegaRepository.deleteById(id);
    }

    private BodegaDTO.Response mapToResponse(Bodega b) {
        return BodegaDTO.Response.builder()
                .id(b.getId())
                .nombre(b.getNombre())
                .ubicacion(b.getUbicacion())
                .telefono(b.getTelefono())
                .responsable(b.getResponsable())
                .activo(b.getActivo())
                .build();
    }
}
