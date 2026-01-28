package com.backend.sri.service;

import com.backend.sri.dto.FormaPagoDTO;
import com.backend.sri.exception.BusinessRuleException;
import com.backend.sri.exception.ResourceNotFoundException;
import com.backend.sri.model.FormaPago;
import com.backend.sri.repository.FormaPagoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FormaPagoService {

    private final FormaPagoRepository formaPagoRepository;

    @PostConstruct
    public void init() {
        if (formaPagoRepository.count() == 0) {
            // Inicializar formas de pago comunes del SRI
            saveIfNotExists("01", "SIN UTILIZACION DEL SISTEMA FINANCIERO");
            saveIfNotExists("15", "COMPENSACIÓN DE DEUDAS");
            saveIfNotExists("16", "TARJETA DE DÉBITO");
            saveIfNotExists("17", "DINERO ELECTRÓNICO");
            saveIfNotExists("18", "TARJETA PREPAGO");
            saveIfNotExists("19", "TARJETA DE CRÉDITO");
            saveIfNotExists("20", "OTROS CON UTILIZACION DEL SISTEMA FINANCIERO");
            saveIfNotExists("21", "ENDOSO DE TÍTULOS");

        }
    }

    private void saveIfNotExists(String codigo, String descripcion) {
        if (!formaPagoRepository.existsByCodigo(codigo)) {
            formaPagoRepository.save(FormaPago.builder()
                    .codigo(codigo)
                    .descripcion(descripcion)
                    .activo(true)
                    .build());
        }
    }

    @Transactional(readOnly = true)
    public List<FormaPagoDTO.Response> findAll() {
        return formaPagoRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FormaPagoDTO.Response findById(Long id) {
        FormaPago formaPago = formaPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forma de pago no encontrada con ID: " + id));
        return mapToResponse(formaPago);
    }

    @Transactional
    public FormaPagoDTO.Response create(FormaPagoDTO.Request request) {
        if (formaPagoRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessRuleException("Ya existe una forma de pago con el código: " + request.getCodigo());
        }

        FormaPago formaPago = FormaPago.builder()
                .codigo(request.getCodigo())
                .descripcion(request.getDescripcion())
                .activo(true)
                .build();

        return mapToResponse(formaPagoRepository.save(formaPago));
    }

    private FormaPagoDTO.Response mapToResponse(FormaPago fp) {
        return FormaPagoDTO.Response.builder()
                .id(fp.getId())
                .codigo(fp.getCodigo())
                .descripcion(fp.getDescripcion())
                .activo(fp.getActivo())
                .build();
    }
}
