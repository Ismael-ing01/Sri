package com.backend.sri.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "empresas")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El RUC es obligatorio")
    @Size(min = 13, max = 13, message = "El RUC debe tener 13 caracteres")
    @Column(nullable = false, unique = true, length = 13)
    private String ruc;

    @NotBlank(message = "La razón social es obligatoria")
    @Column(name = "razon_social", nullable = false)
    private String razonSocial;

    @NotBlank(message = "El nombre comercial es obligatorio")
    @Column(name = "nombre_comercial", nullable = false)
    private String nombreComercial;

    @NotBlank(message = "La dirección matriz es obligatoria")
    @Column(name = "direccion_matriz", nullable = false)
    private String direccionMatriz;

    @Column(name = "direccion_establecimiento")
    private String direccionEstablecimiento;

    @Column(name = "contribuyente_especial")
    private String contribuyenteEspecial;

    @NotNull(message = "El campo obligado a llevar contabilidad es obligatorio")
    @Column(name = "obligado_contabilidad", nullable = false)
    private Boolean obligadoContabilidad;

    @Column(name = "ruta_firma")
    private String rutaFirma;

    @Column(name = "clave_firma")
    private String claveFirma;

    @Column(name = "ruta_logo")
    private String rutaLogo;

    @NotNull(message = "El ambiente es obligatorio (1: Pruebas, 2: Producción)")
    @Column(nullable = false)
    private Integer ambiente; // 1: Pruebas, 2: Producción

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;
}
