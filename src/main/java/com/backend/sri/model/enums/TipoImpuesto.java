package com.backend.sri.model.enums;

import lombok.Getter;

@Getter
public enum TipoImpuesto {
    IVA("2", "IVA"),
    ICE("3", "ICE"),
    IRBPNR("5", "IRBPNR");

    private final String codigo;
    private final String descripcion;

    TipoImpuesto(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }
}
