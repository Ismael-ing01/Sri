package com.backend.sri.controller;

import com.backend.sri.dto.InventarioDTO;
import com.backend.sri.service.InventarioService;
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
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "Gestión de stock en bodegas")
public class InventarioController {

    private final InventarioService inventarioService;

    @PostMapping("/ajuste")
    @Operation(summary = "Realizar movimiento de inventario (Ingreso/Egreso)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ajuste realizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente o datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Producto o bodega no encontrados")
    })
    public ResponseEntity<InventarioDTO.Response> ajustarStock(
            @Valid @RequestBody InventarioDTO.AdjustmentRequest request) {
        return ResponseEntity.ok(inventarioService.ajustarStock(request));
    }

    @GetMapping("/bodega/{bodegaId}")
    @Operation(summary = "Ver stock por bodega")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario de bodega recuperado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Bodega no encontrada")
    })
    public ResponseEntity<List<InventarioDTO.Response>> findByBodega(@PathVariable Long bodegaId) {
        return ResponseEntity.ok(inventarioService.findByBodega(bodegaId));
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Ver stock por producto (en todas las bodegas)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario de producto recuperado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<List<InventarioDTO.Response>> findByProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(inventarioService.findByProducto(productoId));
    }
}
