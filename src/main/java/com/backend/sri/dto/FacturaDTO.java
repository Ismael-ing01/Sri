package com.backend.sri.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class FacturaDTO {

    @Data
    @Builder
    public static class Request {
        @NotNull(message = "El cliente es obligatorio")
        private Long clienteId;

        @NotNull(message = "La forma de pago es obligatoria")
        private Long formaPagoId;

        // Opcional: descuento global si se implementara

        @NotEmpty(message = "La factura debe tener productos")
        @Valid
        private List<DetalleRequest> detalles;
    }

    @Data
    @Builder
    public static class DetalleRequest {
        @NotNull(message = "El producto es obligatorio")
        private Long productoId;

        @NotNull(message = "La cantidad es obligatoria")
        private Integer cantidad;

        // El precio se toma del sistema, pero se podría permitir override si hay
        // permisos
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private ClienteDTO.Response cliente;
        private String secuencial;
        private LocalDateTime fechaEmision;
        private String claveAcceso;
        private String estado;
        private String formaPagoDescripcion;
        private BigDecimal subtotal12;
        private BigDecimal subtotal0;
        private BigDecimal totalIva;
        private BigDecimal totalDescuento;
        private BigDecimal total;
        private List<DetalleResponse> detalles;
        private String mensajeSri;
    }

    @Data
    @Builder
    public static class DetalleResponse {
        private String productoCodigo;
        private String productoNombre;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal descuento;
        private BigDecimal valorIva;
        private BigDecimal precioTotal;
    }
}
