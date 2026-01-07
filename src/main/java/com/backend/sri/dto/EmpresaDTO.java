package com.backend.sri.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

public class EmpresaDTO {

    @Data
    @Builder
    public static class Request {
        @NotBlank(message = "El RUC es obligatorio")
        @Size(min = 13, max = 13, message = "El RUC debe tener 13 caracteres")
        private String ruc;

        @NotBlank(message = "La razón social es obligatoria")
        private String razonSocial;

        @NotBlank(message = "El nombre comercial es obligatorio")
        private String nombreComercial;

        @NotBlank(message = "La dirección matriz es obligatoria")
        private String direccionMatriz;

        private String direccionEstablecimiento;

        private String contribuyenteEspecial;

        @NotNull(message = "El campo obligado a llevar contabilidad es obligatorio")
        private Boolean obligadoContabilidad;

        private String rutaFirma;

        private String claveFirma;

        private String rutaLogo;

        @NotNull(message = "El ambiente es obligatorio")
        @Min(value = 1, message = "Ambiente inválido (1: Pruebas, 2: Producción)")
        @Max(value = 2, message = "Ambiente inválido (1: Pruebas, 2: Producción)")
        private Integer ambiente;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private String ruc;
        private String razonSocial;
        private String nombreComercial;
        private String direccionMatriz;
        private String direccionEstablecimiento;
        private String contribuyenteEspecial;
        private Boolean obligadoContabilidad;
        private String rutaFirma;
        // No devolvemos la clave de la firma por seguridad
        private String rutaLogo;
        private Integer ambiente;
        private Boolean activo;
    }
}
