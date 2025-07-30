package com.LibreriaCreativa.LibreriaCreativa.repository;

import com.LibreriaCreativa.LibreriaCreativa.model.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByOrderId(Integer orderId);
}
