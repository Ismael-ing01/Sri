package com.backend.sri.repository;

import com.backend.sri.model.Impuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImpuestoRepository extends JpaRepository<Impuesto, Long> {
    Optional<Impuesto> findByCodigoAndCodigoPorcentaje(String codigo, String codigoPorcentaje);

    // Para buscar el impuesto por defecto (normalmente IVA vigente)
    Optional<Impuesto> findByEsDefaultTrue();
}
