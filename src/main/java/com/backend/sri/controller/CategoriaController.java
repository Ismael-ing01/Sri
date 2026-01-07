package com.backend.sri.controller;

import com.backend.sri.dto.CategoriaDTO;
import com.backend.sri.service.CategoriaService;
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
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Gestión de categorías de productos")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    @Operation(summary = "Listar todas las categorías")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de categorías recuperada exitosamente")
    })
    public ResponseEntity<List<CategoriaDTO.Response>> findAll() {
        return ResponseEntity.ok(categoriaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<CategoriaDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado")
    })
    public ResponseEntity<CategoriaDTO.Response> create(@Valid @RequestBody CategoriaDTO.Request request) {
        return ResponseEntity.ok(categoriaService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<CategoriaDTO.Response> update(@PathVariable Long id,
            @Valid @RequestBody CategoriaDTO.Request request) {
        return ResponseEntity.ok(categoriaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoría eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
