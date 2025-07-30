package com.LibreriaCreativa.LibreriaCreativa.service;

import com.LibreriaCreativa.LibreriaCreativa.model.Order;
import com.LibreriaCreativa.LibreriaCreativa.model.OrderDetail;
import com.LibreriaCreativa.LibreriaCreativa.repository.OrderDetailRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailService {
    @Autowired
    private OrderDetailRepository orderDetailRepo;
    
    public OrderDetail guardar(OrderDetail orderDetail){
        return orderDetailRepo.save(orderDetail);
    }
    
    public List<OrderDetail> buscarPorPedido(Order order){
        return orderDetailRepo.findByPedido(order);
    }
}
