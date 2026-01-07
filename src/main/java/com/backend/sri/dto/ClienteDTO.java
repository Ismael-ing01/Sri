package com.backend.sri.dto;

import com.backend.sri.model.enums.TipoIdentificacion;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

public class ClienteDTO {

    @Data
    @Builder
    public static class Request {

        @NotNull(message = "El tipo de identificación es obligatorio (RUC, CEDULA, etc)")
        private TipoIdentificacion tipoIdentificacion;

        @NotBlank(message = "La identificación es obligatoria")
        @Size(min = 10, max = 13, message = "La identificación debe tener entre 10 y 13 caracteres")
        private String identificacion;

        @NotBlank(message = "El nombre es obligatorio")
        private String nombre;

        @NotBlank(message = "El apellido es obligatorio")
        private String apellido;

        @NotBlank(message = "La dirección es obligatoria")
        private String direccion;

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        private String email;

        @NotBlank(message = "El telefono es obligatorio")
        @Size(min = 10, max = 10, message = "El telefono debe tener exactamente 10 caracteres")
        private String telefono;
    }

    @Data
    @Builder
    public static class Response {
        private Long id;
        private TipoIdentificacion tipoIdentificacion;
        private String identificacion;
        private String nombre;
        private String apellido;
        private String direccion;
        private String email;
        private String telefono;
    }
}
