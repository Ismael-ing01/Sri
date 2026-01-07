package com.backend.sri.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "secuenciales")
public class Secuencial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código es obligatorio")
    @Column(nullable = false, unique = true) // Ejemplo: FACTURA, NOTA_CREDITO
    private String codigo;

    @NotBlank(message = "El establecimiento es obligatorio")
    @Column(nullable = false, length = 3)
    private String establecimiento;

    @NotBlank(message = "El punto de emisión es obligatorio")
    @Column(name = "punto_emision", nullable = false, length = 3)
    private String puntoEmision;

    @NotNull(message = "El secuencial actual es obligatorio")
    @Column(name = "secuencial_actual", nullable = false)
    private Long secuencialActual;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    // Método utilitario para formatear
    public String getNumeroCompleto() {
        return String.format("%s-%s-%09d", establecimiento, puntoEmision, secuencialActual);
    }
}
