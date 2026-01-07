package com.backend.sri.service;

import com.backend.sri.exception.BusinessRuleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Base64;

@Service
@Slf4j
public class SriClientService {

    // URL Pruebas Offline
    private static final String URL_RECEPCION_PRUEBAS = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
    private static final String URL_AUTORIZACION_PRUEBAS = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";

    // URL Produccion Offline
    private static final String URL_RECEPCION_PRODUCCION = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
    private static final String URL_AUTORIZACION_PRODUCCION = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";

    private final RestTemplate restTemplate;

    public SriClientService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Envía el XML firmado al SRI (validar y recibir)
     */
    public String enviarComprobante(byte[] xmlFirmado, boolean esProduccion) {
        String url = esProduccion ? URL_RECEPCION_PRODUCCION : URL_RECEPCION_PRUEBAS;
        String xmlBase64 = Base64.getEncoder().encodeToString(xmlFirmado);

        // Construir Envelope SOAP manual
        String soapRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ec=\"http://ec.gob.sri.ws.recepcion\">"
                +
                "   <soapenv:Header/>" +
                "   <soapenv:Body>" +
                "      <ec:validarComprobante>" +
                "         <xml>" + xmlBase64 + "</xml>" +
                "      </ec:validarComprobante>" +
                "   </soapenv:Body>" +
                "</soapenv:Envelope>";

        return enviarSoap(url, soapRequest);
    }

    /**
     * Consulta el estado de autorización
     */
    public String consultarAutorizacion(String claveAcceso, boolean esProduccion) {
        String url = esProduccion ? URL_AUTORIZACION_PRODUCCION : URL_AUTORIZACION_PRUEBAS;

        String soapRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ec=\"http://ec.gob.sri.ws.autorizacion\">"
                +
                "   <soapenv:Header/>" +
                "   <soapenv:Body>" +
                "      <ec:autorizacionComprobante>" +
                "         <claveAccesoComprobante>" + claveAcceso + "</claveAccesoComprobante>" +
                "      </ec:autorizacionComprobante>" +
                "   </soapenv:Body>" +
                "</soapenv:Envelope>";

        return enviarSoap(url, soapRequest);
    }

    private String enviarSoap(String url, String soapBody) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_XML);
            headers.set("charset", "utf-8"); // Importante para SOAP

            HttpEntity<String> request = new HttpEntity<>(soapBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BusinessRuleException("Error HTTP SRI: " + response.getStatusCode());
            }
            return response.getBody();

        } catch (Exception e) {
            log.error("Error conectando al SRI", e);
            throw new BusinessRuleException("Error conectando al SRI: " + e.getMessage());
        }
    }
}
