package com.backend.sri.service;

import com.backend.sri.dto.ImpuestoDTO;
import com.backend.sri.exception.ResourceNotFoundException;
import com.backend.sri.model.Impuesto;
import com.backend.sri.repository.ImpuestoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImpuestoService {

    private final ImpuestoRepository impuestoRepository;

    @PostConstruct
    public void init() {
        if (impuestoRepository.count() == 0) {
            // Inicializar IVA 15% (Vigente 2024 - Codigo 2, Porcentaje 4)
            // Nota: Los códigos de porcentaje del SRI varían, asumiremos 4 para 15% como
            // ejemplo común actual
            impuestoRepository.save(Impuesto.builder()
                    .codigo("2") // IVA
                    .codigoPorcentaje("4") // 15%
                    .porcentaje(new BigDecimal("15.00"))
                    .descripcion("IVA 15%")
                    .activo(true)
                    .esDefault(true)
                    .build());

            // IVA 0% (Codigo 2, Porcentaje 0)
            impuestoRepository.save(Impuesto.builder()
                    .codigo("2") // IVA
                    .codigoPorcentaje("0") // 0%
                    .porcentaje(BigDecimal.ZERO)
                    .descripcion("IVA 0%")
                    .activo(true)
                    .esDefault(false)
                    .build());
        }
    }

    @Transactional(readOnly = true)
    public List<ImpuestoDTO.Response> findAll() {
        return impuestoRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal getPorcentajeIvaVigente() {
        return impuestoRepository.findByEsDefaultTrue()
                .map(Impuesto::getPorcentaje)
                .orElse(new BigDecimal("15.00")); // Fallback si no hay config
    }

    private ImpuestoDTO.Response mapToResponse(Impuesto i) {
        return ImpuestoDTO.Response.builder()
                .id(i.getId())
                .codigo(i.getCodigo())
                .codigoPorcentaje(i.getCodigoPorcentaje())
                .porcentaje(i.getPorcentaje())
                .descripcion(i.getDescripcion())
                .activo(i.getActivo())
                .esDefault(i.getEsDefault())
                .build();
    }
}
