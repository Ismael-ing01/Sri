package com.backend.sri.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Service
public class XmlService {

    public String convertToXml(Object source, Class<?>... classesToBeBound) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(classesToBeBound);
        Marshaller marshaller = context.createMarshaller();

        // Formateo bonito
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        // Encoding UTF-8
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        // No generar el header standalone="yes" a veces problemático
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);

        StringWriter writer = new StringWriter();
        marshaller.marshal(source, writer);
        return writer.toString();
    }
}
