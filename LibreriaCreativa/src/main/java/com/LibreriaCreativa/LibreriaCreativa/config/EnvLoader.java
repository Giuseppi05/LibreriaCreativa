package com.LibreriaCreativa.LibreriaCreativa.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class EnvLoader implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        // Solo cargar .env si existe (local)
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        if (!dotenv.entries().isEmpty()) {
            Map<String, Object> envMap = new HashMap<>();
            dotenv.entries().forEach(entry -> envMap.put(entry.getKey(), entry.getValue()));

            context.getEnvironment().getPropertySources()
                    .addFirst(new MapPropertySource("dotenv", envMap));

            System.out.println("✅ .env cargado (modo local)");
        } else {
            System.out.println("⚡ Usando variables de entorno del sistema (Render)");
        }
    }
}
