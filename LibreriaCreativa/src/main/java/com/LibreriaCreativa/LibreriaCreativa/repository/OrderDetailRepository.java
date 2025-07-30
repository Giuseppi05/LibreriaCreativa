package com.LibreriaCreativa.LibreriaCreativa.repository;

import com.LibreriaCreativa.LibreriaCreativa.model.DetallePedidoId;
import com.LibreriaCreativa.LibreriaCreativa.model.Order;
import com.LibreriaCreativa.LibreriaCreativa.model.OrderDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, DetallePedidoId>{
    public List<OrderDetail> findByPedido(Order order);
}