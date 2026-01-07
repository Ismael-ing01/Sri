package com.backend.sri.service;

import com.backend.sri.dto.ClienteDTO;
import com.backend.sri.exception.BusinessRuleException;
import com.backend.sri.exception.ResourceNotFoundException;
import com.backend.sri.model.Cliente;
import com.backend.sri.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<ClienteDTO.Response> findAll() {
        return clienteRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)

    public ClienteDTO.Response findById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
        return mapToResponse(cliente);
    }

    @Transactional(readOnly = true)
    public ClienteDTO.Response findByIdentificacion(String identificacion) {
        Cliente cliente = clienteRepository.findByIdentificacion(identificacion)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con identificación: " + identificacion));
        return mapToResponse(cliente);
    }

    @Transactional

    public ClienteDTO.Response create(ClienteDTO.Request request) {
        // Validación estricta de identificación
        String errorValidacion = com.backend.sri.util.ValidadorIdentificacion.validar(
                request.getIdentificacion(), request.getTipoIdentificacion());

        if (errorValidacion != null) {
            throw new BusinessRuleException(errorValidacion);
        }

        if (clienteRepository.existsByIdentificacion(request.getIdentificacion())) {
            throw new BusinessRuleException(
                    "Ya existe un cliente con la identificación: " + request.getIdentificacion());
        }

        Cliente cliente = Cliente.builder()
                .tipoIdentificacion(request.getTipoIdentificacion())
                .identificacion(request.getIdentificacion())
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .direccion(request.getDireccion())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .build();

        return mapToResponse(clienteRepository.save(cliente));
    }

    @Transactional

    public ClienteDTO.Response update(Long id, ClienteDTO.Request request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Cliente no encontrado para actualizar con ID: " + id));

        // Validación estricta solo si cambia la identificación o tipo
        if (!cliente.getIdentificacion().equals(request.getIdentificacion()) ||
                cliente.getTipoIdentificacion() != request.getTipoIdentificacion()) {

            String errorValidacion = com.backend.sri.util.ValidadorIdentificacion.validar(
                    request.getIdentificacion(), request.getTipoIdentificacion());
            if (errorValidacion != null) {
                throw new BusinessRuleException(errorValidacion);
            }
        }

        // Validate if identification changed and if new one exists
        if (!cliente.getIdentificacion().equals(request.getIdentificacion()) &&
                clienteRepository.existsByIdentificacion(request.getIdentificacion())) {
            throw new BusinessRuleException("La nueva identificación ya está registrada por otro cliente.");
        }

        cliente.setTipoIdentificacion(request.getTipoIdentificacion());
        cliente.setIdentificacion(request.getIdentificacion());
        cliente.setNombre(request.getNombre());
        cliente.setApellido(request.getApellido());
        cliente.setDireccion(request.getDireccion());
        cliente.setEmail(request.getEmail());
        cliente.setTelefono(request.getTelefono());

        return mapToResponse(clienteRepository.save(cliente));
    }

    @Transactional

    public void delete(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente no encontrado para eliminar con ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    private ClienteDTO.Response mapToResponse(Cliente c) {
        return ClienteDTO.Response.builder()
                .id(c.getId())
                .tipoIdentificacion(c.getTipoIdentificacion())
                .identificacion(c.getIdentificacion())
                .nombre(c.getNombre())
                .apellido(c.getApellido())
                .direccion(c.getDireccion())
                .email(c.getEmail())
                .telefono(c.getTelefono())
                .build();
    }
}
