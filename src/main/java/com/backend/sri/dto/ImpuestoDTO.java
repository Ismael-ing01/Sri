package com.backend.sri.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

public class ImpuestoDTO {

    @Data
    @Builder
    public static class Request {
        private String codigo;
        private String codigoPorcentaje;
        @NotNull
        private BigDecimal porcentaje;
        private String descripcion;
        private Boolean esDefault;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private String codigo;
        private String codigoPorcentaje;
        private BigDecimal porcentaje;
        private String descripcion;
        private Boolean activo;
        private Boolean esDefault;
    }
}
