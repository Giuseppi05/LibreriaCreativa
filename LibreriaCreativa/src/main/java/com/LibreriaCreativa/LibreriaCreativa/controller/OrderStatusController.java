package com.LibreriaCreativa.LibreriaCreativa.controller;

import com.LibreriaCreativa.LibreriaCreativa.model.OrderStatus;
import com.LibreriaCreativa.LibreriaCreativa.service.OrderStatusService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orderstatus")
public class OrderStatusController {
    @Autowired
    OrderStatusService statusSrv;
    
    @GetMapping("/all")
    public List<OrderStatus> ObtenerTodo(){
        return statusSrv.listarTodo();
    }
}
