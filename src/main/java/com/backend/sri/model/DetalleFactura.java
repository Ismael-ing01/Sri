package com.backend.sri.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "detalles_factura")
public class DetalleFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "factura_id", nullable = false)
    @JsonIgnore // Evitar recursión infinita en serialización
    @ToString.Exclude
    private Factura factura;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    // Guardamos el precio del producto AL MOMENTO de la venta (snapshot)
    @Column(name = "precio_unitario", nullable = false)
    private BigDecimal precioUnitario;

    @Column(name = "descuento", nullable = false)
    private BigDecimal descuento;

    @Column(name = "precio_total_sin_impuesto", nullable = false)
    private BigDecimal precioTotalSinImpuesto;

    @Column(name = "valor_iva", nullable = false)
    private BigDecimal valorIva;
}
