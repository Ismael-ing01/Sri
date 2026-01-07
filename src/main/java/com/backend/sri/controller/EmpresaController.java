package com.backend.sri.controller;

import com.backend.sri.dto.EmpresaDTO;
import com.backend.sri.service.EmpresaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empresa")
@RequiredArgsConstructor
@Tag(name = "Empresa", description = "Configuración del emisor y firma electrónica")
public class EmpresaController {

    private final EmpresaService empresaService;

    @GetMapping
    @Operation(summary = "Obtener configuración de la empresa (Emisor)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuración recuperada exitosamente"),
            @ApiResponse(responseCode = "404", description = "No hay configuración registrada")
    })
    public ResponseEntity<EmpresaDTO.Response> getEmpresa() {
        // En un sistema single-tenant típico, solo hay una empresa.
        // Podríamos usar findFirst() o obligar pasar ID.
        // Para facilitar, usaremos findFirst().
        return ResponseEntity.ok(empresaService.findFirst());
    }

    @PostMapping
    @Operation(summary = "Registrar datos de la empresa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa registrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o RUC duplicado")
    })
    public ResponseEntity<EmpresaDTO.Response> create(@Valid @RequestBody EmpresaDTO.Request request) {
        return ResponseEntity.ok(empresaService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de la empresa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Empresa no encontrada")
    })
    public ResponseEntity<EmpresaDTO.Response> update(@PathVariable Long id,
            @Valid @RequestBody EmpresaDTO.Request request) {
        return ResponseEntity.ok(empresaService.update(id, request));
    }
}
