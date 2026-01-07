package com.backend.sri.repository;

import com.backend.sri.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Optional<Factura> findByClaveAcceso(String claveAcceso);

    Optional<Factura> findBySecuencial(String secuencial);

    List<Factura> findByClienteIdentificacion(String identificacion);

    List<Factura> findByFechaEmisionBetween(LocalDateTime start, LocalDateTime end);
}
