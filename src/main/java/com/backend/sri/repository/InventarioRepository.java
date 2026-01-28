package com.backend.sri.repository;

import com.backend.sri.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByProductoIdAndBodegaId(Long productoId, Long bodegaId);

    @Query("""
                SELECT i FROM Inventario i
                JOIN FETCH i.producto
                JOIN FETCH i.bodega
                WHERE i.bodega.id = :bodegaId
            """)
    List<Inventario> findByBodegaId(@Param("bodegaId") Long bodegaId);

    @Query("""
                SELECT i FROM Inventario i
                JOIN FETCH i.producto
                JOIN FETCH i.bodega
                WHERE i.producto.id = :productoId
            """)
    List<Inventario> findByProductoId(@Param("productoId") Long productoId);
}
