package com.LibreriaCreativa.LibreriaCreativa;

import com.LibreriaCreativa.LibreriaCreativa.config.EnvLoader;
import java.util.TimeZone;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class LibreriaCreativaApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));

        new SpringApplicationBuilder(LibreriaCreativaApplication.class)
                .initializers(new EnvLoader()) // ✅ Registramos el Initializer
                .run(args);
    }

}
