package com.backend.sri.repository;

import com.backend.sri.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Optional<Inventario> findByProductoIdAndBodegaId(Long productoId, Long bodegaId);

    List<Inventario> findByBodegaId(Long bodegaId);

    List<Inventario> findByProductoId(Long productoId);
}
