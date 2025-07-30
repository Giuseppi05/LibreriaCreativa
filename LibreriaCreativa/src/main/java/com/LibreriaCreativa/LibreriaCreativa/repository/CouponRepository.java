package com.LibreriaCreativa.LibreriaCreativa.repository;

import com.LibreriaCreativa.LibreriaCreativa.model.Coupon;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    
    Optional<Coupon> findByCodigoCupon(String codigoCupon);
}