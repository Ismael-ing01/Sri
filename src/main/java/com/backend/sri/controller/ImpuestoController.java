package com.backend.sri.controller;

import com.backend.sri.dto.ImpuestoDTO;
import com.backend.sri.service.ImpuestoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/impuestos")
@RequiredArgsConstructor
@Tag(name = "Impuestos", description = "Gestión de tarifas e impuestos SRI (IVA, ICE, etc)")
public class ImpuestoController {

    private final ImpuestoService impuestoService;

    @GetMapping
    @Operation(summary = "Listar impuestos configurados")
    public ResponseEntity<List<ImpuestoDTO.Response>> findAll() {
        return ResponseEntity.ok(impuestoService.findAll());
    }
}
