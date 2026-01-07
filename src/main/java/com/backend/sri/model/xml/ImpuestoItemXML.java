package com.backend.sri.model.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class ImpuestoItemXML {

    @XmlElement(required = true)
    private String codigo;

    @XmlElement(required = true)
    private String codigoPorcentaje;

    @XmlElement(required = true)
    private BigDecimal tarifa; // Ej: 15.00

    @XmlElement(required = true)
    private BigDecimal baseImponible;

    @XmlElement(required = true)
    private BigDecimal valor;
}
