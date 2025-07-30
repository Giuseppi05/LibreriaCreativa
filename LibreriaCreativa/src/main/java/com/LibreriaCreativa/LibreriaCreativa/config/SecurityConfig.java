package com.LibreriaCreativa.LibreriaCreativa.config;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import com.LibreriaCreativa.LibreriaCreativa.service.UsuarioDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.context.annotation.Bean;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UsuarioDetailsService uds;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    public SecurityConfig(UsuarioDetailsService uds, JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.uds = uds;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"No autorizado\"}");
            }))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/**",
                    
                    "/api/products/catalogo",
                    "/api/products/product/**",
                    "/api/products/dest",
                    
                    "/api/messages/save",
                    
                    "/api/category/all"
                ).permitAll()
                .requestMatchers(
                    "/api/order/save",
                    "/api/order/mine",
                    "/api/order/detail",
                    "/api/order/cancelarUser/**",
                    "/api/coupon/coupon/**"
                ).hasRole("USER")
                .requestMatchers(
                    "/api/products/save",    
                    "/api/products/delete/**",    
                    "/api/products/admin",
                    
                    "/api/category/save",
                    "/api/category/update",
                    "/api/category/delete/**",
                    "/api/category/admin",
                    
                    "/api/order/admin",
                    "/api/order/status/**",
                    
                    "/api/coupon/admin",
                    "/api/coupon/delete/**",
                    "/api/coupon/save",
                    "/api/coupon/update",
                    
                    "/api/messages/order",
                    
                    "/api/orderstatus/all"
                ).hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:4200"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
