package com.gelco.repository;

import com.gelco.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoTrue();
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
    long countByActivoTrue();
    List<Producto> findByStockLessThanEqualAndActivoTrue(int umbral);
}
