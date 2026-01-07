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
@Table(name = "inventario", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "producto_id", "bodega_id" })
})
public class Inventario extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_id", nullable = false)
    private Bodega bodega;

    @Column(nullable = false)
    @Builder.Default
    private Integer cantidad = 0;
}
