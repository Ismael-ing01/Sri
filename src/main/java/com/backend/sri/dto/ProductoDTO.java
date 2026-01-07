package com.backend.sri.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

public class ProductoDTO {

    @Data
    @Builder
    public static class Request {
        @NotBlank(message = "El código principal es obligatorio")
        private String codigoPrincipal;

        @NotBlank(message = "El nombre es obligatorio")
        private String nombre;

        private String descripcion;

        @NotNull(message = "El precio de compra es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
        private BigDecimal precioCompra;

        @NotNull(message = "El margen de ganancia es obligatorio")
        @DecimalMin(value = "0.0", message = "El margen no puede ser negativo")
        private BigDecimal margenGanancia;

        @NotNull(message = "El ID de categoría es obligatorio")
        private Long categoriaId;

        @Builder.Default
        private Boolean tieneIva = true;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private String codigoPrincipal;
        private String nombre;
        private String descripcion;
        private BigDecimal precioCompra;
        private BigDecimal margenGanancia;
        private BigDecimal precioVenta; // Calculated
        private Boolean tieneIva;
        private String categoriaNombre;
        private Boolean activo;
    }
}
