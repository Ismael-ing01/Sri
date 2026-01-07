package com.backend.sri.model.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "factura")
@XmlType(propOrder = {
        "infoTributaria",
        "infoFactura",
        "detalles",
        "infoAdicional" // Opcional
})
public class FacturaXML {

    @XmlAttribute
    private String id; // "comprobante"

    @XmlAttribute
    private String version; // "1.0.0" (o 1.1.0)

    @XmlElement(required = true)
    private InfoTributaria infoTributaria;

    @XmlElement(required = true)
    private InfoFactura infoFactura;

    @XmlElementWrapper(name = "detalles")
    @XmlElement(name = "detalle")
    private List<DetalleXML> detalles;

    // TODO: infoAdicional si se requiere
    private String infoAdicional;
}
