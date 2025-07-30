package com.LibreriaCreativa.LibreriaCreativa.repository;

import com.LibreriaCreativa.LibreriaCreativa.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    Page<Order> findByUserId(Integer userId, Pageable pageable);
}
