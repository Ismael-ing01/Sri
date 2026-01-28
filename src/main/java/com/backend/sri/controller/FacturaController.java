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
    private final com.backend.sri.service.PdfService pdfService;

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
            @ApiResponse(responseCode = "200", description = "Proceso completo"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    public ResponseEntity<?> enviarSri(@PathVariable Long id) {
        try {
            String resultado = facturaService.enviarFacturaSri(id);
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("message", "Proceso enviado al SRI");
            response.put("sriResponse", resultado);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            java.util.Map<String, String> error = new java.util.HashMap<>();
            error.put("message", "Error al enviar al SRI: " + e.getMessage());
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping(value = "/{id}/pdf", produces = "application/pdf")
    @Operation(summary = "Generar RIDE (PDF)", responses = {
            @ApiResponse(responseCode = "200", description = "PDF generado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada")
    })
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        com.backend.sri.model.Factura factura = facturaService.getEntityById(id);
        byte[] pdfContent = pdfService.generateFacturaPdf(factura);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "factura-" + factura.getSecuencial() + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfContent, headers, org.springframework.http.HttpStatus.OK);
    }
}
