package com.LibreriaCreativa.LibreriaCreativa.service;

import com.LibreriaCreativa.LibreriaCreativa.model.Messages;
import com.LibreriaCreativa.LibreriaCreativa.repository.MessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class MessagesService {
    @Autowired
    private MessagesRepository mesRep;
    
    public Messages guardarMensaje(Messages msj){
        return mesRep.save(msj);
    }
    
    public Page<Messages> obtenerMensajesPaginados(int page, int size) {
        return mesRep.findAll(PageRequest.of(page, size));
    }
    
}
