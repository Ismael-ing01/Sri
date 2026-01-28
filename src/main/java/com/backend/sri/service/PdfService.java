package com.backend.sri.service;

import com.backend.sri.model.DetalleFactura;
import com.backend.sri.model.Factura;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfService {

    private final com.backend.sri.repository.EmpresaRepository empresaRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public byte[] generateFacturaPdf(Factura factura) {
        try {
            // 1. Obtener Datos Empresa
            com.backend.sri.model.Empresa empresa = empresaRepository.findByRuc(factura.getEmpresaRuc())
                    .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

            // 2. Cargar Plantilla
            InputStream templateStream = new ClassPathResource("reports/factura.jrxml").getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);

            // 3. Mapear Parámetros
            Map<String, Object> parameters = new HashMap<>();

            // Cabecera Empresa
            parameters.put("RUC", empresa.getRuc());
            parameters.put("RAZON_SOCIAL", empresa.getRazonSocial());
            parameters.put("DIR_MATRIZ", empresa.getDireccionMatriz());
            parameters.put("DIR_SUCURSAL",
                    empresa.getDireccionEstablecimiento() != null ? empresa.getDireccionEstablecimiento()
                            : empresa.getDireccionMatriz());
            parameters.put("CONTRIBUYENTE_ESPECIAL", empresa.getContribuyenteEspecial());
            parameters.put("OBLIGADO_CONTABILIDAD",
                    Boolean.TRUE.equals(empresa.getObligadoContabilidad()) ? "SI" : "NO");
            parameters.put("LOGO_PATH", empresa.getRutaLogo()); // Puede ser null, Jasper maneja nulls en imagenes con
                                                                // onErrorType="Blank"

            // Cabecera Factura
            parameters.put("NUM_FACTURA", factura.getSecuencial()); // 001-001-000000001
            parameters.put("NUM_AUTORIZACION", factura.getClaveAcceso()); // En Offline, es la misma clave de acceso
            parameters.put("FECHA_AUTORIZACION", factura.getFechaEmision().format(DATE_TIME_FORMATTER)); // TODO: Usar
                                                                                                         // fecha real
                                                                                                         // de
                                                                                                         // autorización
                                                                                                         // si existe
            parameters.put("AMBIENTE", empresa.getAmbiente() == 1 ? "PRUEBAS" : "PRODUCCIÓN");
            parameters.put("TIPO_EMISION", "NORMAL");
            parameters.put("CLAVE_ACCESO", factura.getClaveAcceso());

            // Cliente
            parameters.put("CLIENTE_NOMBRES",
                    factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());
            parameters.put("CLIENTE_IDENTIFICACION", factura.getCliente().getIdentificacion());
            parameters.put("FECHA_EMISION", factura.getFechaEmision().format(DATE_FORMATTER));
            parameters.put("GUIA_REMISION", ""); // TODO: Implementar si aplica

            // Totales
            parameters.put("SUBTOTAL_12", factura.getSubtotal12());
            parameters.put("SUBTOTAL_0", factura.getSubtotal0());
            parameters.put("SUBTOTAL_NO_OBJETO", BigDecimal.ZERO); // TODO: Agregar campos en Factura si se requiere
            parameters.put("SUBTOTAL_EXENTO", BigDecimal.ZERO);
            parameters.put("SUBTOTAL_SIN_IMPUESTOS", factura.getSubtotalSinImpuestos());
            parameters.put("TOTAL_DESCUENTO", factura.getTotalDescuento());
            parameters.put("ICE", BigDecimal.ZERO);
            parameters.put("IVA_12", factura.getTotalIva());
            parameters.put("PROPINA", BigDecimal.ZERO);
            parameters.put("VALOR_TOTAL", factura.getTotal());

            // 4. Mapear Detalles (DataSource)
            // Necesitamos una clase DTO simple para los detalles o un mapa
            List<Map<String, Object>> detalles = factura.getDetalles().stream().map(this::mapDetalle)
                    .collect(Collectors.toList());
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(detalles);

            // 5. Generar PDF
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (Exception e) {
            log.error("Error generando PDF RIDE", e);
            throw new RuntimeException("Error al generar el PDF de la factura: " + e.getMessage());
        }
    }

    private Map<String, Object> mapDetalle(DetalleFactura d) {
        Map<String, Object> map = new HashMap<>();
        map.put("cantidad", BigDecimal.valueOf(d.getCantidad()));
        map.put("descripcion", d.getProducto().getNombre());
        map.put("codigoPrincipal", d.getProducto().getCodigoPrincipal());
        map.put("codigoAuxiliar", d.getProducto().getCodigoPrincipal());
        map.put("precioUnitario", d.getPrecioUnitario());
        map.put("descuento", d.getDescuento());
        map.put("precioTotalSinImpuesto", d.getPrecioTotalSinImpuesto());
        return map;
    }
}
