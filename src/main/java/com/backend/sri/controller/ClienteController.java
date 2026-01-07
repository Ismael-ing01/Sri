package com.backend.sri.controller;

import com.backend.sri.dto.ClienteDTO;
import com.backend.sri.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteDTO.Response>> getAll() {
        return ResponseEntity.ok(clienteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.findById(id));
    }

    @GetMapping("/identificacion/{identificacion}")
    public ResponseEntity<ClienteDTO.Response> getByIdentificacion(@PathVariable String identificacion) {
        return ResponseEntity.ok(clienteService.findByIdentificacion(identificacion));
    }

    @PostMapping
    public ResponseEntity<ClienteDTO.Response> create(@Valid @RequestBody ClienteDTO.Request request) {
        return new ResponseEntity<>(clienteService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO.Response> update(@PathVariable Long id,
            @Valid @RequestBody ClienteDTO.Request request) {
        return ResponseEntity.ok(clienteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
