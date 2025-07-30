package com.LibreriaCreativa.LibreriaCreativa.service;

import com.LibreriaCreativa.LibreriaCreativa.model.User;
import com.LibreriaCreativa.LibreriaCreativa.repository.UserRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService{
    
    private final UserRepository repo;

    public UsuarioDetailsService(UserRepository repo) {
        this.repo = repo;
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = repo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(
                "No existe un usuario con email: " + email));

        List<GrantedAuthority> authorities = u.getAdmin()
            ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            : Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        return org.springframework.security.core.userdetails.User.builder()
            .username(u.getEmail())
            .password(u.getPassword())
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build();
    }
    
}
