package com.backend.sri.repository;

import com.backend.sri.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByCodigoPrincipal(String codigoPrincipal);

    boolean existsByCodigoPrincipal(String codigoPrincipal);

    List<Producto> findByCategoriaId(Long categoriaId);
}
