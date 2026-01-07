package com.backend.sri.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

public class BodegaDTO {

    @Data
    @Builder
    public static class Request {
        @NotBlank(message = "El nombre es obligatorio")
        private String nombre;
        private String ubicacion;
        private String telefono;
        private String responsable;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private String nombre;
        private String ubicacion;
        private String telefono;
        private String responsable;
        private Boolean activo;
    }
}
