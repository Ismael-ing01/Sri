package com.backend.sri.model.enums;

import lombok.Getter;

@Getter
public enum TipoComprobante {
    FACTURA("01", "Factura"),
    NOTA_DE_CREDITO("04", "Nota de Crédito"),
    NOTA_DE_DEBITO("05", "Nota de Débito"),
    GUIA_REMISION("06", "Guía de Remisión"),
    COMPROBANTE_RETENCION("07", "Comprobante de Retención");

    private final String codigo;
    private final String descripcion;

    TipoComprobante(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }
}
