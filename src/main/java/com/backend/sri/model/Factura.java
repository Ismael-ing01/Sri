package com.backend.sri.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Almacenamos el RUC de la empresa emisora al momento de la factura (snapshot)
    @Column(name = "empresa_ruc", nullable = false)
    private String empresaRuc;

    @Column(name = "secuencial", nullable = false, unique = true)
    private String secuencial; // Formato 001-001-000000001

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "clave_acceso", length = 49, unique = true)
    private String claveAcceso;

    // Estados: PENDIENTE, FIRMADA, AUTORIZADA, RECHAZADA, ANULADA
    @Column(nullable = false)
    private String estado;

    // Forma de Pago (Relación N:1)
    @ManyToOne
    @JoinColumn(name = "forma_pago_id", nullable = false)
    private FormaPago formaPago;

    // Campos de totales
    @Column(nullable = false)
    private BigDecimal subtotalSinImpuestos;

    @Column(name = "subtotal_12", nullable = false)
    private BigDecimal subtotal12;

    @Column(name = "subtotal_0", nullable = false)
    private BigDecimal subtotal0;

    @Column(name = "total_descuento", nullable = false)
    private BigDecimal totalDescuento;

    @Column(name = "total_iva", nullable = false)
    private BigDecimal totalIva;

    @Column(nullable = false)
    private BigDecimal total;

    // Relación OneToMany con DetalleFactura
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleFactura> detalles;

    // Mensaje de respuesta del SRI (para casos de error)
    @Column(name = "mensaje_sri", length = 1000)
    private String mensajeSri;

    // Ruta del archivo XML generado y firmado
    @Column(name = "ruta_xml")
    private String rutaXml;
}
