package com.backend.sri.model;

import com.backend.sri.model.audit.DateAudit;
import com.backend.sri.model.enums.TipoIdentificacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "clientes", uniqueConstraints = {
        @UniqueConstraint(columnNames = "identificacion")
})
public class Cliente extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoIdentificacion tipoIdentificacion;

    @Column(nullable = false, length = 13, unique = true)
    private String identificacion;

    @Column(length = 150)
    private String nombre;

    @Column(length = 150)
    private String apellido;

    @Column(length = 300)
    private String direccion;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String telefono;
}
