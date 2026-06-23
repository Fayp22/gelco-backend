package com.gelco.repository;

import com.gelco.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    @Query("SELECT d FROM DetallePedido d JOIN FETCH d.producto WHERE d.pedido.id = :pedidoId")
    List<DetallePedido> findByPedidoId(@Param("pedidoId") Long pedidoId);

    @Query("SELECT d.producto.id, SUM(d.cantidad) as totalCantidad " +
           "FROM DetallePedido d " +
           "WHERE d.pedido.fecha >= :fechaDesde " +
           "AND d.pedido.estado = 'Entregado' " +
           "GROUP BY d.producto.id " +
           "ORDER BY totalCantidad DESC")
    List<Object[]> findVentasAgrupadasPorProducto(@Param("fechaDesde") LocalDateTime fechaDesde);

    @Query("SELECT d.producto.id, SUM(d.cantidad) as totalCantidad " +
           "FROM DetallePedido d " +
           "WHERE d.pedido.estado = 'Entregado' " +
           "GROUP BY d.producto.id " +
           "ORDER BY totalCantidad DESC")
    List<Object[]> findVentasAgrupadasPorProductoTodos();
}
