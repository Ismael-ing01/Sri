package com.backend.sri.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

public class FormaPagoDTO {

    @Data
    @Builder
    public static class Request {
        @NotBlank(message = "El código es obligatorio")
        private String codigo;

        @NotBlank(message = "La descripción es obligatoria")
        private String descripcion;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private String codigo;
        private String descripcion;
        private Boolean activo;
    }
}
