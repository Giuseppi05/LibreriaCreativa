package com.LibreriaCreativa.LibreriaCreativa.service;

import com.LibreriaCreativa.LibreriaCreativa.model.Payment;
import com.LibreriaCreativa.LibreriaCreativa.repository.PaymentRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Autowired
    PaymentRepository paymentRepo;
    
    public List<Payment> buscarPorCodigoDeOrden(Integer c){
        return paymentRepo.findByOrderId(c);
    }
    
    public Payment guardar(Payment p){
        return paymentRepo.save(p);
    }
}
