package com.gelco.repository;

import com.gelco.model.InventarioMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventarioMovimientoRepository extends JpaRepository<InventarioMovimiento, Integer> {

    @Query("SELECT im FROM InventarioMovimiento im JOIN FETCH im.producto ORDER BY im.fecha DESC")
    List<InventarioMovimiento> findAllWithProducto();

}