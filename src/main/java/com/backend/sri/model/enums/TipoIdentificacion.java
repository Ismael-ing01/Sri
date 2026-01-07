package com.backend.sri.model.enums;

import lombok.Getter;

@Getter
public enum TipoIdentificacion {
    RUC("04"),
    CEDULA("05"),
    PASAPORTE("06"),
    CONSUMIDOR_FINAL("07");

    private final String codigo;

    TipoIdentificacion(String codigo) {
        this.codigo = codigo;
    }
}
