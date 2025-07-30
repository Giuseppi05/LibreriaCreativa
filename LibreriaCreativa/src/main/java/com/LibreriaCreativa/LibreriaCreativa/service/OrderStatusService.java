package com.LibreriaCreativa.LibreriaCreativa.service;

import com.LibreriaCreativa.LibreriaCreativa.model.OrderStatus;
import com.LibreriaCreativa.LibreriaCreativa.repository.OrderStatusRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusService {
    @Autowired
    private OrderStatusRepository orderStatusRepo;
    
    public Optional<OrderStatus> findById(Integer id){
        return orderStatusRepo.findById(id);
    }
    
    public List<OrderStatus> listarTodo(){
        return orderStatusRepo.findAll();
    }
}