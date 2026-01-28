package com.backend.sri.model.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "fechaEmision",
        "dirEstablecimiento",
        "contribuyenteEspecial",
        "obligadoContabilidad",
        "tipoIdentificacionComprador",
        "razonSocialComprador",
        "identificacionComprador",
        "totalSinImpuestos",
        "totalDescuento",
        "totalImpuestos",
        "propina",
        "importeTotal",
        "moneda",
        "pagos"
})
public class InfoFactura {

    @XmlElement(required = true)
    private String fechaEmision;

    private String dirEstablecimiento;

    private String contribuyenteEspecial;

    private String obligadoContabilidad;

    @XmlElement(required = true)
    private String tipoIdentificacionComprador;

    @XmlElement(required = true)
    private String razonSocialComprador;

    @XmlElement(required = true)
    private String identificacionComprador;

    @XmlElement(required = true)
    private BigDecimal totalSinImpuestos;

    @XmlElement(required = true)
    private BigDecimal totalDescuento;

    @XmlElementWrapper(name = "totalConImpuestos")
    @XmlElement(name = "totalImpuesto")
    private List<TotalImpuestoXML> totalImpuestos;

    @XmlElement(required = true)
    private BigDecimal propina;

    @XmlElement(required = true)
    private BigDecimal importeTotal;

    @XmlElement(required = true)
    private String moneda;

    @XmlElementWrapper(name = "pagos")
    @XmlElement(name = "pago")
    private List<PagoXML> pagos;
}
