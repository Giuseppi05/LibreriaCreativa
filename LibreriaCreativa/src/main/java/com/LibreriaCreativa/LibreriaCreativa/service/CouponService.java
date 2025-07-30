package com.LibreriaCreativa.LibreriaCreativa.service;

import com.LibreriaCreativa.LibreriaCreativa.model.Coupon;
import com.LibreriaCreativa.LibreriaCreativa.repository.CouponRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class CouponService {

    @Autowired
    CouponRepository couponRepo;

    public Optional<Coupon> buscarPorCodigo(String c) {
        return couponRepo.findByCodigoCupon(c);
    }

    public Page<Coupon> obtenerCuponesPaginados(int page, int size) {
        return couponRepo.findAll(PageRequest.of(page, size));
    }

    public void borrarPorId(Integer id) {
        couponRepo.deleteById(id);
    }
    
    public Optional<Coupon> buscarPorId(Integer id){
        return couponRepo.findById(id);
    }
    
    public Coupon guardar(Coupon c){
        return couponRepo.save(c);
    }

}
