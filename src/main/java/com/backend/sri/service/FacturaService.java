package com.backend.sri.service;

import com.backend.sri.dto.ClienteDTO;
import com.backend.sri.dto.FacturaDTO;
import com.backend.sri.dto.InventarioDTO;
import com.backend.sri.exception.BusinessRuleException;
import com.backend.sri.exception.ResourceNotFoundException;
import com.backend.sri.model.*;
import com.backend.sri.repository.*;
import com.backend.sri.util.ClaveAccesoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final EmpresaRepository empresaRepository;
    private final FormaPagoRepository formaPagoRepository;
    private final SecuencialRepository secuencialRepository;
    private final SecuencialService secuencialService;
    private final InventarioService inventarioService;
    private final ImpuestoService impuestoService;
    private final com.backend.sri.mapper.FacturaXmlMapper facturaXmlMapper;
    private final XmlService xmlService;
    private final FirmaElectronicaService firmaElectronicaService;
    private final SriClientService sriClientService;

    @Transactional
    public FacturaDTO.Response create(FacturaDTO.Request request) {
        // ... (Validations 1-3 remain same)
        // 1. Validar Cliente
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        // 2. Validar Forma Pago
        FormaPago formaPago = formaPagoRepository.findById(request.getFormaPagoId())
                .orElseThrow(() -> new ResourceNotFoundException("Forma de pago no encontrada"));

        // 3. Obtener Configuración de Empresa
        Empresa empresa = empresaRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new BusinessRuleException(
                        "No hay configuración de empresa registrada. Configure el emisor primero."));

        if (!Boolean.TRUE.equals(empresa.getActivo())) {
            throw new BusinessRuleException("La empresa emisora no está activa.");
        }

        // 4. Obtener y Auto-Incrementar Secuencial
        Secuencial secuencialConfig = secuencialService.incrementAndGet("FACTURA");
        String numeroSecuencialFormateado = String.format("%09d", secuencialConfig.getSecuencialActual());
        String serie = secuencialConfig.getEstablecimiento() + secuencialConfig.getPuntoEmision();
        String secuencialCompleto = secuencialConfig.getNumeroCompleto();

        // 5. Preparar Cabecera
        Factura factura = Factura.builder()
                .cliente(cliente)
                .empresaRuc(empresa.getRuc())
                .secuencial(secuencialCompleto)
                .fechaEmision(LocalDateTime.now())
                .estado("PENDIENTE")
                .formaPago(formaPago)
                .detalles(new ArrayList<>())
                .build();

        // 6. Generar Clave de Acceso
        Date fechaDate = Date.from(factura.getFechaEmision().atZone(ZoneId.systemDefault()).toInstant());
        String claveAcceso = ClaveAccesoUtil.generarClaveAcceso(
                fechaDate,
                "01",
                empresa.getRuc(),
                String.valueOf(empresa.getAmbiente()),
                serie,
                numeroSecuencialFormateado);
        factura.setClaveAcceso(claveAcceso);

        // 7. Procesar Detalles
        BigDecimal subtotal12 = BigDecimal.ZERO;
        BigDecimal subtotal0 = BigDecimal.ZERO;
        BigDecimal totalDescuento = BigDecimal.ZERO;
        BigDecimal totalIva = BigDecimal.ZERO;

        // [NEW] Obtener porcentaje vigente dinámicamente
        BigDecimal porcentajeIva = impuestoService.getPorcentajeIvaVigente().divide(new BigDecimal("100"));

        for (FacturaDTO.DetalleRequest detReq : request.getDetalles()) {
            Producto producto = productoRepository.findById(detReq.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado ID: " + detReq.getProductoId()));

            InventarioDTO.AdjustmentRequest egreso = InventarioDTO.AdjustmentRequest.builder()
                    .productoId(producto.getId())
                    .bodegaId(1L)
                    .cantidad(detReq.getCantidad())
                    .esIngreso(false)
                    .build();
            inventarioService.ajustarStock(egreso);

            BigDecimal precioUnitario = producto.getPrecioVenta();
            BigDecimal cantidad = BigDecimal.valueOf(detReq.getCantidad());
            BigDecimal precioTotalSinImpuesto = precioUnitario.multiply(cantidad);
            BigDecimal descuentoItem = BigDecimal.ZERO;

            BigDecimal ivaItem = BigDecimal.ZERO;
            if (Boolean.TRUE.equals(producto.getTieneIva())) {
                subtotal12 = subtotal12.add(precioTotalSinImpuesto);
                // [CHANGED] Use dynamic percentage
                ivaItem = precioTotalSinImpuesto.multiply(porcentajeIva);
                totalIva = totalIva.add(ivaItem);
            } else {
                subtotal0 = subtotal0.add(precioTotalSinImpuesto);
            }

            DetalleFactura detalle = DetalleFactura.builder()
                    .factura(factura)
                    .producto(producto)
                    .cantidad(detReq.getCantidad())
                    .precioUnitario(precioUnitario)
                    .descuento(descuentoItem)
                    .precioTotalSinImpuesto(precioTotalSinImpuesto)
                    .valorIva(ivaItem)
                    .build();

            factura.getDetalles().add(detalle);
        }

        // Setear Totales finales
        factura.setSubtotal12(subtotal12);
        factura.setSubtotal0(subtotal0);
        factura.setSubtotalSinImpuestos(subtotal12.add(subtotal0));
        factura.setTotalDescuento(totalDescuento);
        factura.setTotalIva(totalIva);
        factura.setTotal(factura.getSubtotalSinImpuestos().subtract(totalDescuento).add(totalIva));

        // 8. Guardar Factura (Cascada guardará detalles)
        Factura facturaGuardada = facturaRepository.save(factura);

        // TODO: En el futuro aquí se llamaría al servicio de generación de XML y Firma
        // Async

        return mapToResponse(facturaGuardada);
    }

    @Transactional(readOnly = true)
    public List<FacturaDTO.Response> findAll() {
        return facturaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FacturaDTO.Response findById(Long id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada ID: " + id));
        return mapToResponse(factura);
    }

    private FacturaDTO.Response mapToResponse(Factura f) {
        List<FacturaDTO.DetalleResponse> detallesRes = f.getDetalles().stream()
                .map(d -> FacturaDTO.DetalleResponse.builder()
                        .productoCodigo(d.getProducto().getCodigoPrincipal())
                        .productoNombre(d.getProducto().getNombre())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .descuento(d.getDescuento())
                        .valorIva(d.getValorIva())
                        .precioTotal(d.getPrecioTotalSinImpuesto().add(d.getValorIva()))
                        .build())
                .collect(Collectors.toList());

        return FacturaDTO.Response.builder()
                .id(f.getId())
                .cliente(ClienteDTO.Response.builder()
                        .identificacion(f.getCliente().getIdentificacion())
                        .nombre(f.getCliente().getNombre())
                        .apellido(f.getCliente().getApellido())
                        .email(f.getCliente().getEmail())
                        .direccion(f.getCliente().getDireccion())
                        .telefono(f.getCliente().getTelefono())
                        .tipoIdentificacion(f.getCliente().getTipoIdentificacion())
                        .build())
                .secuencial(f.getSecuencial())
                .fechaEmision(f.getFechaEmision())
                .claveAcceso(f.getClaveAcceso())
                .estado(f.getEstado())
                .formaPagoDescripcion(f.getFormaPago().getDescripcion())
                .subtotal12(f.getSubtotal12())
                .subtotal0(f.getSubtotal0())
                .totalIva(f.getTotalIva())
                .totalDescuento(f.getTotalDescuento())
                .total(f.getTotal())
                .mensajeSri(f.getMensajeSri())
                .detalles(detallesRes)
                .build();
    }

    @Transactional(readOnly = true)
    public String generateXml(Long facturaId) {
        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada ID: " + facturaId));

        try {
            com.backend.sri.model.xml.FacturaXML facturaXML = facturaXmlMapper.toXml(factura);
            return xmlService.convertToXml(facturaXML, com.backend.sri.model.xml.FacturaXML.class);
        } catch (Exception e) {
            throw new BusinessRuleException("Error al generar XML: " + e.getMessage());
        }
    }

    @Transactional
    public String enviarFacturaSri(Long facturaId) {
        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada ID: " + facturaId));

        if ("AUTORIZADO".equals(factura.getEstado())) {
            throw new BusinessRuleException("La factura ya está autorizada.");
        }

        Empresa empresa = empresaRepository.findByRuc(factura.getEmpresaRuc())
                .orElseThrow(() -> new BusinessRuleException("Empresa no encontrada"));

        try {
            // 1. Generar XML
            com.backend.sri.model.xml.FacturaXML facturaXML = facturaXmlMapper.toXml(factura);
            String xmlContent = xmlService.convertToXml(facturaXML, com.backend.sri.model.xml.FacturaXML.class);

            // 2. Firmar XML
            byte[] xmlFirmado = firmaElectronicaService.firmarXml(xmlContent, empresa);

            // (Opcional: Guardar en disco para debug)
            // Files.write(Paths.get("firmados/" + factura.getClaveAcceso() + ".xml"),
            // xmlFirmado);

            // 3. Enviar a Recepción SRI
            boolean esProduccion = empresa.getAmbiente() == 2;
            String respuestaRecepcion = sriClientService.enviarComprobante(xmlFirmado, esProduccion);

            // Validar respuesta recepción (Parseo básico de "RECIBIDA")
            if (respuestaRecepcion.contains("RECIBIDA")) {
                factura.setEstado("EN_PROCESO");
                // 4. Inmediatamente consultar autorización (a veces tarda unos segundos)
                Thread.sleep(3000); // Espera prudencial 3s
                String respuestaAutorizacion = sriClientService.consultarAutorizacion(factura.getClaveAcceso(),
                        esProduccion);

                if (respuestaAutorizacion.contains("AUTORIZADO")) {
                    factura.setEstado("AUTORIZADO");
                    factura.setMensajeSri("Factura Autorizada Exitosamente");
                } else {
                    factura.setEstado("RECHAZADO"); // O Pendiente
                    factura.setMensajeSri("Recibida, pero no autorizada aun. Revise SOAP: " + respuestaAutorizacion);
                }
                return respuestaAutorizacion;
            } else {
                factura.setEstado("DEVUELTA");
                factura.setMensajeSri("Devuelta por Recepción: " + respuestaRecepcion);
                return respuestaRecepcion;
            }

        } catch (Exception e) {
            factura.setEstado("ERROR_INTERNO");
            factura.setMensajeSri("Error: " + e.getMessage());
            throw new BusinessRuleException("Error en proceso SRI: " + e.getMessage());
        } finally {
            facturaRepository.save(factura);
        }
    }
}
