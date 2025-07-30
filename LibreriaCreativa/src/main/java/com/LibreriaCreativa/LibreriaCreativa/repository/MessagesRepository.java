package com.LibreriaCreativa.LibreriaCreativa.repository;

import com.LibreriaCreativa.LibreriaCreativa.model.Messages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessagesRepository extends JpaRepository<Messages, Integer> {
  
}
