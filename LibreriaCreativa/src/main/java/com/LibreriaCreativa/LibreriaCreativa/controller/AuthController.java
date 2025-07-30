package com.LibreriaCreativa.LibreriaCreativa.controller;

import com.LibreriaCreativa.LibreriaCreativa.model.LoginRequest;
import com.LibreriaCreativa.LibreriaCreativa.model.User;
import com.LibreriaCreativa.LibreriaCreativa.service.UserService;
import com.LibreriaCreativa.LibreriaCreativa.util.JwtUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String PASSWORD_PATTERN
            = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";

    @PostMapping(path = "/register", consumes = "application/json")
    public ResponseEntity<?> registrarUsuario(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> userExisting = userService.findByEmail(user.getEmail());
        if (userExisting.isPresent()) {
            response.put("success", false);
            response.put("message", "El email ya está registrado");
            return ResponseEntity.badRequest().body(response);
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            response.put("success", false);
            response.put("message", "Las contraseñas no coinciden");
            return ResponseEntity.badRequest().body(response);
        }

        if (!validarPassword(user.getPassword())) {
            response.put("success", false);
            response.put("message", "La contraseña debe tener mayúsculas, minúsculas, números y símbolos(@#$%^&+=!)");
            return ResponseEntity.badRequest().body(response);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAdmin(false);
        userService.guardar(user);

        response.put("success", true);
        response.put("message", "Usuario registrado exitosamente");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = userService.findByEmail(request.getEmail()).orElseThrow();

            String token = jwtUtil.generateToken(request.getEmail(), user.getAdmin());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", token);
            response.put("username", user.getEmail());
            response.put("admin", user.getAdmin());

            return ResponseEntity.ok(response);

        } catch (AuthenticationException ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Credenciales inválidas");
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.getUsernameFromToken(token);

        if (email == null || !jwtUtil.validateToken(token, email)) {
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }

        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", true);
        response.put("admin", user.getAdmin());
        response.put("username", user.getEmail());

        return ResponseEntity.ok(response);
    }

    private boolean validarPassword(String password) {
        return password != null && password.matches(PASSWORD_PATTERN);
    }
}
