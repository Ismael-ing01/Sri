package com.backend.sri.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

public class InventarioDTO {

    @Data
    @Builder
    public static class AdjustmentRequest {
        @NotNull(message = "Producto ID es requerido")
        private Long productoId;

        @NotNull(message = "Bodega ID es requerido")
        private Long bodegaId;

        @NotNull(message = "La cantidad es requerida")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        private Integer cantidad;

        // true = add, false = subtract
        @Builder.Default
        private Boolean esIngreso = true;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private String productoNombre;
        private String productoCodigo;
        private String bodegaNombre;
        private Integer cantidadActual;
    }
}
