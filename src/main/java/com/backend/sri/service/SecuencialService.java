package com.backend.sri.service;

import com.backend.sri.dto.SecuencialDTO;
import com.backend.sri.exception.BusinessRuleException;
import com.backend.sri.exception.ResourceNotFoundException;
import com.backend.sri.model.Secuencial;
import com.backend.sri.repository.SecuencialRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SecuencialService {

    private final SecuencialRepository secuencialRepository;

    @PostConstruct
    public void init() {
        // Inicializar secuencial por defecto para FACTURA si no existe
        if (!secuencialRepository.existsByCodigo("FACTURA")) {
            secuencialRepository.save(Secuencial.builder()
                    .codigo("FACTURA")
                    .establecimiento("001")
                    .puntoEmision("001")
                    .secuencialActual(0L) // Empieza en 0, el siguiente será 1
                    .activo(true)
                    .build());
        }
    }

    @Transactional(readOnly = true)
    public List<SecuencialDTO.Response> findAll() {
        return secuencialRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SecuencialDTO.Response findById(Long id) {
        Secuencial secuencial = secuencialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Secuencial no encontrado con ID: " + id));
        return mapToResponse(secuencial);
    }

    @Transactional
    public SecuencialDTO.Response create(SecuencialDTO.Request request) {
        if (secuencialRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessRuleException("Ya existe un secuencial para el código: " + request.getCodigo());
        }

        Secuencial secuencial = Secuencial.builder()
                .codigo(request.getCodigo())
                .establecimiento(request.getEstablecimiento())
                .puntoEmision(request.getPuntoEmision())
                .secuencialActual(request.getSecuencialActual())
                .activo(true)
                .build();

        return mapToResponse(secuencialRepository.save(secuencial));
    }

    @Transactional
    public SecuencialDTO.Response update(Long id, SecuencialDTO.Request request) {
        Secuencial secuencial = secuencialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Secuencial no encontrado con ID: " + id));

        // No permitimos cambiar el código si ya existe otro igual (aunque normalmente
        // el código es fijo)
        if (!secuencial.getCodigo().equals(request.getCodigo())
                && secuencialRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessRuleException("El código " + request.getCodigo() + " ya está en uso");
        }

        secuencial.setCodigo(request.getCodigo());
        secuencial.setEstablecimiento(request.getEstablecimiento());
        secuencial.setPuntoEmision(request.getPuntoEmision());
        secuencial.setSecuencialActual(request.getSecuencialActual());

        return mapToResponse(secuencialRepository.save(secuencial));
    }

    // Método transaccional crítico con aislamiento SERIALIZABLE o PESSIMISTIC_WRITE
    // si fuera necesario
    // Para simplificar usaremos Transactional default, pero en alta concurrencia se
    // debe bloquear la fila.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Secuencial incrementAndGet(String codigo) {
        Secuencial secuencial = secuencialRepository.findByCodigo(codigo)
                .orElseThrow(
                        () -> new ResourceNotFoundException("No existe configuración de secuencial para: " + codigo));

        secuencial.setSecuencialActual(secuencial.getSecuencialActual() + 1);
        return secuencialRepository.save(secuencial);
    }

    private SecuencialDTO.Response mapToResponse(Secuencial s) {
        // Calculamos el "siguiente" para visualizar, aunque el getNumeroCompleto usa el
        // actual
        String siguiente = String.format("%s-%s-%09d", s.getEstablecimiento(), s.getPuntoEmision(),
                s.getSecuencialActual() + 1);
        return SecuencialDTO.Response.builder()
                .id(s.getId())
                .codigo(s.getCodigo())
                .establecimiento(s.getEstablecimiento())
                .puntoEmision(s.getPuntoEmision())
                .secuencialActual(s.getSecuencialActual())
                .siguienteNumeroFormateado(siguiente)
                .build();
    }
}
