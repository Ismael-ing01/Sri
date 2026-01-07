package com.backend.sri.mapper;

import com.backend.sri.model.Factura;
import com.backend.sri.model.xml.FacturaXML;

public interface FacturaXmlMapper {
    FacturaXML toXml(Factura factura);
}
