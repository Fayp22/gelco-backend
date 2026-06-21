package com.gelco.repository;

import com.gelco.model.Consultora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultoraRepository extends JpaRepository<Consultora, Long> {
    Optional<Consultora> findByUsuarioId(Long usuarioId);

    List<Consultora> findByNivel(String nivel);

    @Query("SELECT c FROM Consultora c JOIN c.usuario u WHERE u.estado = :estado")
    List<Consultora> findByUsuarioEstado(@Param("estado") Boolean estado);

    @Query("SELECT c FROM Consultora c WHERE c.nivel = :nivel AND c.ventasTotales >= :minVentas")
    List<Consultora> findByNivelAndMinVentas(@Param("nivel") String nivel, @Param("minVentas") java.math.BigDecimal minVentas);

    long countByNivel(String nivel);
}