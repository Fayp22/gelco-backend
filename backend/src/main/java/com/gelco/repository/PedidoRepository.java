package com.gelco.repository;

import com.gelco.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByConsultoraId(Long consultoraId);
    List<Pedido> findByClienteId(Long clienteId);
    List<Pedido> findByEstado(String estado);
}
