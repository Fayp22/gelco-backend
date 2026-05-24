package com.gelco.repository;

import com.gelco.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente JOIN FETCH p.consultora c JOIN FETCH c.usuario WHERE p.consultora.id = :consultoraId")
    List<Pedido> findByConsultoraId(@Param("consultoraId") Long consultoraId);

    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente JOIN FETCH p.consultora c JOIN FETCH c.usuario WHERE p.cliente.id = :clienteId")
    List<Pedido> findByClienteId(@Param("clienteId") Long clienteId);

    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente JOIN FETCH p.consultora c JOIN FETCH c.usuario WHERE p.estado = :estado")
    List<Pedido> findByEstado(@Param("estado") String estado);

    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente JOIN FETCH p.consultora c JOIN FETCH c.usuario WHERE p.id = :id")
    Optional<Pedido> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente JOIN FETCH p.consultora c JOIN FETCH c.usuario")
    List<Pedido> findAllWithRelations();

    long countByClienteId(Long clienteId);
    boolean existsByClienteIdAndEstado(Long clienteId, String estado);
    long countByConsultoraIdAndEstado(Long consultoraId, String estado);
    long countByEstado(String estado);
}