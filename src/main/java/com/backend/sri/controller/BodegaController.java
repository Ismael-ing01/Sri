package com.backend.sri.controller;

import com.backend.sri.dto.BodegaDTO;
import com.backend.sri.service.BodegaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bodegas")
@RequiredArgsConstructor
@Tag(name = "Bodegas", description = "Gestión de bodegas y sucursales")
public class BodegaController {

    private final BodegaService bodegaService;

    @GetMapping
    @Operation(summary = "Listar todas las bodegas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de bodegas recuperada exitosamente")
    })
    public ResponseEntity<List<BodegaDTO.Response>> findAll() {
        return ResponseEntity.ok(bodegaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener bodega por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bodega encontrada"),
            @ApiResponse(responseCode = "404", description = "Bodega no encontrada")
    })
    public ResponseEntity<BodegaDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(bodegaService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva bodega")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bodega creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado")
    })
    public ResponseEntity<BodegaDTO.Response> create(@Valid @RequestBody BodegaDTO.Request request) {
        return ResponseEntity.ok(bodegaService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar bodega existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bodega actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado"),
            @ApiResponse(responseCode = "404", description = "Bodega no encontrada")
    })
    public ResponseEntity<BodegaDTO.Response> update(@PathVariable Long id,
            @Valid @RequestBody BodegaDTO.Request request) {
        return ResponseEntity.ok(bodegaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar bodega")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Bodega eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Bodega no encontrada")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bodegaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
