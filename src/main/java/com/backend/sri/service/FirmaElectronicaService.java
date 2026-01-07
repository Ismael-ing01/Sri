package com.backend.sri.service;

import com.backend.sri.exception.BusinessRuleException;
import com.backend.sri.model.Empresa;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

@Service
public class FirmaElectronicaService {

    /**
     * Firma el XML proporcionado usando el archivo .p12 de la empresa
     * 
     * @param xmlContent XML en formato String
     * @param empresa    Datos de la empresa (ruta firma, clave)
     * @return byte[] del XML firmado
     */
    public byte[] firmarXml(String xmlContent, Empresa empresa) {
        try {
            // 1. Cargar el Keystore (Certificado .p12)
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (FileInputStream fis = new FileInputStream(empresa.getRutaFirma())) {
                keyStore.load(fis, empresa.getClaveFirma().toCharArray());
            }

            // 2. Obtener el alias del certificado
            Enumeration<String> aliases = keyStore.aliases();
            String alias = null;
            while (aliases.hasMoreElements()) {
                alias = aliases.nextElement();
                if (keyStore.isKeyEntry(alias)) {
                    break;
                }
            }
            if (alias == null) {
                throw new BusinessRuleException("No se encontró un alias válido en el archivo de firma.");
            }

            // 3. Obtener Clave Privada y Certificado
            KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias,
                    new KeyStore.PasswordProtection(empresa.getClaveFirma().toCharArray()));
            PrivateKey privateKey = keyEntry.getPrivateKey();
            X509Certificate cert = (X509Certificate) keyEntry.getCertificate();

            // 4. Parsear el XML a Documento DOM
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));

            // 5. Configurar el contexto de firma
            DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement());

            // 6. Crear la factoría de firma XML
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

            // 7. Configurar referencias (Digest SHA1 según estándar SRI XAdES-BES común)
            // Se firma todo el documento ("")
            Reference ref = fac.newReference("", fac.newDigestMethod(DigestMethod.SHA1, null),
                    Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
                    null, null);

            // 8. Configurar SignedInfo (Canonicalization y Signature Method RSA-SHA1)
            SignedInfo si = fac.newSignedInfo(
                    fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                    fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                    Collections.singletonList(ref));

            // 9. KeyInfo (Incluir certificado X509)
            KeyInfoFactory kif = fac.getKeyInfoFactory();
            List<Object> x509Content = List.of(cert);
            X509Data xd = kif.newX509Data(x509Content);
            KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

            // 10. Firmar
            XMLSignature signature = fac.newXMLSignature(si, ki);
            signature.sign(dsc);

            // 11. Convertir documento firmado a byte[]
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            // Configurar salida (Opcional: Indentación no recomendada post-firma para no
            // romper hash)
            trans.transform(new DOMSource(doc), new StreamResult(bos));

            return bos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessRuleException("Error al firmar electrónicamente: " + e.getMessage());
        }
    }

    // Método auxiliar para guardar en disco si es necesario o retornar String
    public String firmarXmlToString(String xmlContent, Empresa empresa) {
        return new String(firmarXml(xmlContent, empresa));
    }
}
