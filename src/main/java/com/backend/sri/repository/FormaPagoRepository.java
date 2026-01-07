package com.backend.sri.repository;

import com.backend.sri.model.FormaPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormaPagoRepository extends JpaRepository<FormaPago, Long> {
    Optional<FormaPago> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}
