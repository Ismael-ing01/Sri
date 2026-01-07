package com.backend.sri.repository;

import com.backend.sri.model.Bodega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BodegaRepository extends JpaRepository<Bodega, Long> {
    Optional<Bodega> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
