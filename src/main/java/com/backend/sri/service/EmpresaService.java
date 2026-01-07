package com.backend.sri.service;

import com.backend.sri.dto.EmpresaDTO;
import com.backend.sri.exception.BusinessRuleException;
import com.backend.sri.exception.ResourceNotFoundException;
import com.backend.sri.model.Empresa;
import com.backend.sri.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public List<EmpresaDTO.Response> findAll() {
        return empresaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmpresaDTO.Response findById(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + id));
        return mapToResponse(empresa);
    }

    @Transactional(readOnly = true)
    public EmpresaDTO.Response findFirst() {
        // Método utilitario para obtener la primera empresa configurada (asumiendo
        // sistema mono-empresa)
        return empresaRepository.findAll().stream().findFirst()
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No existe configuración de empresa registrada"));
    }

    @Transactional
    public EmpresaDTO.Response create(EmpresaDTO.Request request) {
        if (empresaRepository.existsByRuc(request.getRuc())) {
            throw new BusinessRuleException("Ya existe una empresa registrada con el RUC: " + request.getRuc());
        }

        // Validación simple de que la ruta de firma no sea vacía si se proporciona
        // Aquí podríamos validar si el archivo existe en disco, pero como el usuario
        // dijo que "luego lo pondrá",
        // solo guardamos la ruta string.

        Empresa empresa = Empresa.builder()
                .ruc(request.getRuc())
                .razonSocial(request.getRazonSocial())
                .nombreComercial(request.getNombreComercial())
                .direccionMatriz(request.getDireccionMatriz())
                .direccionEstablecimiento(request.getDireccionEstablecimiento())
                .obligadoContabilidad(request.getObligadoContabilidad())
                .contribuyenteEspecial(request.getContribuyenteEspecial())
                .rutaFirma(request.getRutaFirma())
                .claveFirma(request.getClaveFirma())
                .rutaLogo(request.getRutaLogo())
                .ambiente(request.getAmbiente())
                .activo(true)
                .build();

        return mapToResponse(empresaRepository.save(empresa));
    }

    @Transactional
    public EmpresaDTO.Response update(Long id, EmpresaDTO.Request request) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + id));

        if (!empresa.getRuc().equals(request.getRuc()) && empresaRepository.existsByRuc(request.getRuc())) {
            throw new BusinessRuleException("El RUC " + request.getRuc() + " ya pertenece a otra empresa registrada");
        }

        empresa.setRuc(request.getRuc());
        empresa.setRazonSocial(request.getRazonSocial());
        empresa.setNombreComercial(request.getNombreComercial());
        empresa.setDireccionMatriz(request.getDireccionMatriz());
        empresa.setDireccionEstablecimiento(request.getDireccionEstablecimiento());
        empresa.setObligadoContabilidad(request.getObligadoContabilidad());
        empresa.setContribuyenteEspecial(request.getContribuyenteEspecial());
        empresa.setRutaFirma(request.getRutaFirma());

        // Solo actualizamos la clave si viene algo en el request
        if (request.getClaveFirma() != null && !request.getClaveFirma().isBlank()) {
            empresa.setClaveFirma(request.getClaveFirma());
        }

        empresa.setRutaLogo(request.getRutaLogo());
        empresa.setAmbiente(request.getAmbiente());

        return mapToResponse(empresaRepository.save(empresa));
    }

    private EmpresaDTO.Response mapToResponse(Empresa e) {
        return EmpresaDTO.Response.builder()
                .id(e.getId())
                .ruc(e.getRuc())
                .razonSocial(e.getRazonSocial())
                .nombreComercial(e.getNombreComercial())
                .direccionMatriz(e.getDireccionMatriz())
                .direccionEstablecimiento(e.getDireccionEstablecimiento())
                .obligadoContabilidad(e.getObligadoContabilidad())
                .contribuyenteEspecial(e.getContribuyenteEspecial())
                .rutaFirma(e.getRutaFirma())
                .rutaLogo(e.getRutaLogo())
                .ambiente(e.getAmbiente())
                .activo(e.getActivo())
                .build();
    }
}
