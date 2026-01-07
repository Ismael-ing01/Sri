package com.backend.sri.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

public class SecuencialDTO {

    @Data
    @Builder
    public static class Request {
        @NotBlank(message = "El código es obligatorio")
        private String codigo;

        @NotBlank(message = "El establecimiento es obligatorio")
        @Size(min = 3, max = 3, message = "El establecimiento debe tener 3 dígitos")
        private String establecimiento;

        @NotBlank(message = "El punto de emisión es obligatorio")
        @Size(min = 3, max = 3, message = "El punto de emisión debe tener 3 dígitos")
        private String puntoEmision;

        @NotNull(message = "El secuencial actual es obligatorio")
        @Min(value = 0, message = "El secuencial no puede ser negativo")
        private Long secuencialActual;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private String codigo;
        private String establecimiento;
        private String puntoEmision;
        private Long secuencialActual;
        private String siguienteNumeroFormateado;
    }
}
