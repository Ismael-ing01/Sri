package com.backend.sri.model;

import com.backend.sri.model.audit.DateAudit;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "productos")
public class Producto extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigoPrincipal;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(name = "precio_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCompra;

    @Column(name = "margen_ganancia", nullable = false, precision = 5, scale = 2)
    private BigDecimal margenGanancia; // Porcentaje (Ej: 30.00 para 30%)

    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta; // Calculado automáticamente

    @Builder.Default
    @Column(name = "tiene_iva")
    private Boolean tieneIva = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Builder.Default
    private Boolean activo = true;

    /**
     * Calcula automáticamente el Precio de Venta antes de guardar o actualizar.
     * Fórmula: PVP = Costo + (Costo * (Margen / 100))
     */
    @PrePersist
    @PreUpdate
    public void calcularPrecioVenta() {
        if (precioCompra != null && margenGanancia != null) {
            BigDecimal ganancia = precioCompra.multiply(margenGanancia).divide(new BigDecimal(100),
                    RoundingMode.HALF_UP);
            this.precioVenta = precioCompra.add(ganancia).setScale(2, RoundingMode.HALF_UP);
        } else if (precioVenta == null) {
            // Si no hay datos para calcular, asegurarse que no sea nulo (default a costo)
            this.precioVenta = (precioCompra != null) ? precioCompra : BigDecimal.ZERO;
        }
    }
}
