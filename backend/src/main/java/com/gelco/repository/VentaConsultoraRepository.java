package com.gelco.repository;

import com.gelco.model.VentaConsultora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaConsultoraRepository extends JpaRepository<VentaConsultora, Long> {
    List<VentaConsultora> findByConsultoraId(Long consultoraId);

    Optional<VentaConsultora> findByConsultoraIdAndMesAndAnio(Long consultoraId, Integer mes, Integer anio);

    @Query("SELECT v FROM VentaConsultora v WHERE v.consultora.id = :consultoraId AND v.mes = :mes AND v.anio = :anio")
    List<VentaConsultora> buscarVentasPorConsultoraYMes(@Param("consultoraId") Long consultoraId, @Param("mes") Integer mes, @Param("anio") Integer anio);

    @Query("SELECT COALESCE(SUM(v.totalVentas), 0) FROM VentaConsultora v WHERE v.consultora.id = :consultoraId")
    BigDecimal getTotalVentasByConsultora(@Param("consultoraId") Long consultoraId);
}
