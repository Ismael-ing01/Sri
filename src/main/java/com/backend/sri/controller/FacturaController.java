package com.backend.sri.controller;

import com.backend.sri.dto.FacturaDTO;
import com.backend.sri.service.FacturaService;
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
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
@Tag(name = "Facturas", description = "Emisión y gestión de facturas electrónicas")
public class FacturaController {

    private final FacturaService facturaService;

    @PostMapping
    @Operation(summary = "Crear nueva factura")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Factura creada y firmada (simulada) exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o stock insuficiente"),
            @ApiResponse(responseCode = "404", description = "Cliente, Empresa o Producto no encontrados")
    })
    public ResponseEntity<FacturaDTO.Response> create(@Valid @RequestBody FacturaDTO.Request request) {
        return ResponseEntity.ok(facturaService.create(request));
    }

    @GetMapping
    @Operation(summary = "Listar todas las facturas")
    public ResponseEntity<List<FacturaDTO.Response>> findAll() {
        return ResponseEntity.ok(facturaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener factura por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Factura encontrada"),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada")
    })
    public ResponseEntity<FacturaDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.findById(id));
    }

    @GetMapping(value = "/{id}/xml", produces = "application/xml")
    @Operation(summary = "Generar XML de factura (Pre-visualización)", responses = {
            @ApiResponse(responseCode = "200", description = "XML Generado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada")
    })
    public ResponseEntity<String> generateXml(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.generateXml(id));
    }

    @PostMapping("/{id}/enviar-sri")
    @Operation(summary = "Firmar y Enviar Factura al SRI", responses = {
            @ApiResponse(responseCode = "200", description = "Proceso completo (Respuesta SOAP SRI)"),
            @ApiResponse(responseCode = "400", description = "Error en envío")
    })
    public ResponseEntity<String> enviarSri(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.enviarFacturaSri(id));
    }
}
