package com.backend.sri.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "formas_pago")
public class FormaPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código es obligatorio")
    @Column(nullable = false, unique = true)
    private String codigo;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false)
    private String descripcion;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;
}
