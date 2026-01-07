package com.backend.sri.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "impuestos")
public class Impuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código SRI (Ej: 2 para IVA, 3 para ICE)
    @Column(nullable = false, length = 5)
    private String codigo;

    // Código de Porcentaje SRI (Ej: 0 para 0%, 2 para 12%, 4 para 15%)
    @Column(name = "codigo_porcentaje", nullable = false, length = 5)
    private String codigoPorcentaje;

    @NotNull(message = "El porcentaje es obligatorio")
    @Column(nullable = false)
    private BigDecimal porcentaje; // Ej: 15.00

    @Column(nullable = false)
    private String descripcion; // Ej: IVA 15%

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    // Indica si es el impuesto por defecto para nuevos productos/cálculos
    @Builder.Default
    @Column(name = "es_default", nullable = false)
    private Boolean esDefault = false;
}
