package com.LibreriaCreativa.LibreriaCreativa.controller;

import com.LibreriaCreativa.LibreriaCreativa.model.Messages;
import com.LibreriaCreativa.LibreriaCreativa.service.MessagesService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class MessagesController {

    @Autowired
    private MessagesService msjSrv;
    
    @GetMapping("/admin")
    public Page<Messages> obtenerCategoriasAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return msjSrv.obtenerMensajesPaginados(page, size);
    }

    @PostMapping("/save")
    public ResponseEntity<?> nuevo(@RequestBody Messages msj) {
        try {
            msjSrv.guardarMensaje(msj);
            return ResponseEntity.ok(Map.of("success", true, "message", "Mensaje guardado correctamente."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("success", false, "message", "Error al guardar el mensaje."));
        }
    }
 
}
