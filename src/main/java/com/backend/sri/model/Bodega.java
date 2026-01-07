package com.backend.sri.model;

import com.backend.sri.model.audit.DateAudit;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "bodegas")
public class Bodega extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String ubicacion;

    private String telefono;

    private String responsable;

    @Builder.Default
    private Boolean activo = true;
}
