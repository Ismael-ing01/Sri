package com.backend.sri.repository;

import com.backend.sri.model.Secuencial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecuencialRepository extends JpaRepository<Secuencial, Long> {
    Optional<Secuencial> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}
