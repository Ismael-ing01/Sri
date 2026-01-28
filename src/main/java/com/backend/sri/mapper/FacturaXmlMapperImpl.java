package com.backend.sri.mapper;

import com.backend.sri.model.DetalleFactura;
import com.backend.sri.model.Factura;
import com.backend.sri.model.xml.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FacturaXmlMapperImpl implements FacturaXmlMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final com.backend.sri.repository.EmpresaRepository empresaRepository;

    @Override
    public FacturaXML toXml(Factura factura) {
        FacturaXML xml = new FacturaXML();
        xml.setId("comprobante");
        xml.setVersion("1.0.0"); // Version vigente

        // Obtener datos de empresa (Emisor)
        // Se asume single-tenant por MVP, tomamos por RUC
        com.backend.sri.model.Empresa empresa = empresaRepository.findByRuc(factura.getEmpresaRuc())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada para RUC: " + factura.getEmpresaRuc()));

        // 1. Info Tributaria
        InfoTributaria infoTrib = new InfoTributaria();
        infoTrib.setAmbiente(String.valueOf(empresa.getAmbiente()));
        infoTrib.setTipoEmision("1"); // Normal
        infoTrib.setRazonSocial(empresa.getRazonSocial());
        infoTrib.setNombreComercial(empresa.getNombreComercial());
        infoTrib.setRuc(empresa.getRuc());
        infoTrib.setClaveAcceso(factura.getClaveAcceso());
        infoTrib.setCodDoc("01"); // Factura

        String[] parts = factura.getSecuencial().split("-");
        infoTrib.setEstab(parts[0]);
        infoTrib.setPtoEmi(parts[1]);
        infoTrib.setSecuencial(parts[2]);
        infoTrib.setDirMatriz(empresa.getDireccionMatriz());
        xml.setInfoTributaria(infoTrib);

        // 2. Info Factura
        InfoFactura infoFac = new InfoFactura();
        infoFac.setFechaEmision(factura.getFechaEmision().format(DATE_FORMATTER));
        infoFac.setDirEstablecimiento(
                empresa.getDireccionEstablecimiento() != null ? empresa.getDireccionEstablecimiento()
                        : empresa.getDireccionMatriz());
        infoFac.setContribuyenteEspecial(empresa.getContribuyenteEspecial());
        infoFac.setObligadoContabilidad(Boolean.TRUE.equals(empresa.getObligadoContabilidad()) ? "SI" : "NO");

        infoFac.setTipoIdentificacionComprador(
                mapTipoIdentificacion(factura.getCliente().getTipoIdentificacion().name()));
        infoFac.setRazonSocialComprador(factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());
        infoFac.setIdentificacionComprador(factura.getCliente().getIdentificacion());
        infoFac.setTotalSinImpuestos(factura.getSubtotalSinImpuestos().setScale(2, RoundingMode.HALF_UP));
        infoFac.setTotalDescuento(factura.getTotalDescuento().setScale(2, RoundingMode.HALF_UP));
        infoFac.setPropina(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        infoFac.setImporteTotal(factura.getTotal().setScale(2, RoundingMode.HALF_UP));
        infoFac.setMoneda("DOLAR");

        // Pagos
        PagoXML pago = new PagoXML();
        pago.setFormaPago(factura.getFormaPago().getCodigo());
        pago.setTotal(factura.getTotal().setScale(2, RoundingMode.HALF_UP));
        infoFac.setPagos(List.of(pago));

        // Totales Impuestos (Agrupado)
        List<TotalImpuestoXML> totalImpuestos = new ArrayList<>();

        // Total Base 0
        if (factura.getSubtotal0().compareTo(BigDecimal.ZERO) > 0) {
            totalImpuestos.add(TotalImpuestoXML.builder()
                    .codigo("2") // IVA
                    .codigoPorcentaje("0") // 0%
                    .baseImponible(factura.getSubtotal0().setScale(2, RoundingMode.HALF_UP))
                    .valor(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                    .build());
        }

        // Total Base 12/15 (Grabado)
        if (factura.getSubtotal12().compareTo(BigDecimal.ZERO) > 0) {
            totalImpuestos.add(TotalImpuestoXML.builder()
                    .codigo("2") // IVA
                    .codigoPorcentaje("4") // 15% - Quemado por MVP, debería ser dinámico si hay 12% y 15% mezclados
                    .baseImponible(factura.getSubtotal12().setScale(2, RoundingMode.HALF_UP))
                    .valor(factura.getTotalIva().setScale(2, RoundingMode.HALF_UP))
                    .build());
        }
        infoFac.setTotalImpuestos(totalImpuestos);
        xml.setInfoFactura(infoFac);

        // 3. Detalles
        List<DetalleXML> detallesXml = factura.getDetalles().stream()
                .map(this::mapDetalle)
                .collect(Collectors.toList());
        xml.setDetalles(detallesXml);

        return xml;
    }

    private DetalleXML mapDetalle(DetalleFactura d) {
        // Impuestos por item
        ImpuestoItemXML impuestoXml = ImpuestoItemXML.builder()
                .codigo("2") // IVA
                .codigoPorcentaje(d.getValorIva().compareTo(BigDecimal.ZERO) > 0 ? "4" : "0") // TODO: Dinamico
                .tarifa(d.getValorIva().compareTo(BigDecimal.ZERO) > 0 ? new BigDecimal("15.00") : BigDecimal.ZERO) // TODO:
                                                                                                                    // Dinamico
                .baseImponible(d.getPrecioTotalSinImpuesto().setScale(2, RoundingMode.HALF_UP))
                .valor(d.getValorIva().setScale(2, RoundingMode.HALF_UP))
                .build();

        return DetalleXML.builder()
                .codigoPrincipal(d.getProducto().getCodigoPrincipal())
                .codigoAuxiliar(d.getProducto().getCodigoPrincipal()) // Fallback: mismo que principal
                .descripcion(d.getProducto().getNombre())
                .cantidad(BigDecimal.valueOf(d.getCantidad()).setScale(2, RoundingMode.HALF_UP))
                .precioUnitario(d.getPrecioUnitario().setScale(2, RoundingMode.HALF_UP))
                .descuento(d.getDescuento().setScale(2, RoundingMode.HALF_UP))
                .precioTotalSinImpuesto(d.getPrecioTotalSinImpuesto().setScale(2, RoundingMode.HALF_UP))
                .impuestos(List.of(impuestoXml))
                .build();
    }

    private String mapTipoIdentificacion(String tipo) {
        switch (tipo) {
            case "RUC":
                return "04";
            case "CEDULA":
                return "05";
            case "PASAPORTE":
                return "06";
            case "CONSUMIDOR_FINAL":
                return "07";
            default:
                return "07";
        }
    }
}
