package com.LibreriaCreativa.LibreriaCreativa.repository;

import com.LibreriaCreativa.LibreriaCreativa.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer>{
    
}
