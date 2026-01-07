package com.backend.sri.model.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "codigoPrincipal",
        "descripcion",
        "cantidad",
        "precioUnitario",
        "descuento",
        "precioTotalSinImpuesto",
        "impuestos"
})
public class DetalleXML {

    @XmlElement(required = true)
    private String codigoPrincipal;

    @XmlElement(required = true)
    private String descripcion;

    @XmlElement(required = true)
    private BigDecimal cantidad;

    @XmlElement(required = true)
    private BigDecimal precioUnitario;

    @XmlElement(required = true)
    private BigDecimal descuento;

    @XmlElement(required = true)
    private BigDecimal precioTotalSinImpuesto;

    @XmlElementWrapper(name = "impuestos")
    @XmlElement(name = "impuesto")
    private List<ImpuestoItemXML> impuestos;
}
