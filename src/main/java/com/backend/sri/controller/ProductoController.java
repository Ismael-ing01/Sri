package com.backend.sri.controller;

import com.backend.sri.dto.ProductoDTO;
import com.backend.sri.service.ProductoService;
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
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Gestión de catálogo de productos")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    @Operation(summary = "Listar todos los productos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos recuperada exitosamente")
    })
    public ResponseEntity<List<ProductoDTO.Response>> findAll() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductoDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o código duplicado")
    })
    public ResponseEntity<ProductoDTO.Response> create(@Valid @RequestBody ProductoDTO.Request request) {
        return ResponseEntity.ok(productoService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o código duplicado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductoDTO.Response> update(@PathVariable Long id,
            @Valid @RequestBody ProductoDTO.Request request) {
        return ResponseEntity.ok(productoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
