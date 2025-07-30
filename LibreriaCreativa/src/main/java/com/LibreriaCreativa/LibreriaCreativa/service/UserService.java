package com.LibreriaCreativa.LibreriaCreativa.service;

import com.LibreriaCreativa.LibreriaCreativa.model.User;
import com.LibreriaCreativa.LibreriaCreativa.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;
    
    public Optional<User> findByEmail(String email){
        return userRepo.findByEmail(email);
    }
    
    public User guardar(User u){
        return userRepo.save(u);
    }
}
