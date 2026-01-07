package com.backend.sri.controller;

import com.backend.sri.dto.FormaPagoDTO;
import com.backend.sri.service.FormaPagoService;
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
@RequestMapping("/api/formas-pago")
@RequiredArgsConstructor
@Tag(name = "Formas de Pago", description = "Catálogo de formas de pago SRI")
public class FormaPagoController {

    private final FormaPagoService formaPagoService;

    @GetMapping
    @Operation(summary = "Listar todas las formas de pago")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista recuperada exitosamente")
    })
    public ResponseEntity<List<FormaPagoDTO.Response>> findAll() {
        return ResponseEntity.ok(formaPagoService.findAll());
    }

    @PostMapping
    @Operation(summary = "Crear nueva forma de pago")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Código duplicado")
    })
    public ResponseEntity<FormaPagoDTO.Response> create(@Valid @RequestBody FormaPagoDTO.Request request) {
        return ResponseEntity.ok(formaPagoService.create(request));
    }
}
