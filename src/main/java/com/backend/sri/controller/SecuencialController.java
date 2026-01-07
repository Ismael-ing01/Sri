package com.backend.sri.controller;

import com.backend.sri.dto.SecuencialDTO;
import com.backend.sri.service.SecuencialService;
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
@RequestMapping("/api/secuenciales")
@RequiredArgsConstructor
@Tag(name = "Secuenciales", description = "Gestión de numeración de comprobantes")
public class SecuencialController {

    private final SecuencialService secuencialService;

    @GetMapping
    @Operation(summary = "Listar todos los secuenciales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista recuperada exitosamente")
    })
    public ResponseEntity<List<SecuencialDTO.Response>> findAll() {
        return ResponseEntity.ok(secuencialService.findAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar configuración de secuencial")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Secuencial no encontrado")
    })
    public ResponseEntity<SecuencialDTO.Response> update(@PathVariable Long id,
            @Valid @RequestBody SecuencialDTO.Request request) {
        return ResponseEntity.ok(secuencialService.update(id, request));
    }
}
