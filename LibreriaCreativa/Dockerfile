FROM eclipse-temurin:23-jdk
WORKDIR /app

# Copiar jar y .env
COPY target/LibreriaCreativa-0.0.1-SNAPSHOT.jar app.jar
COPY .env .env

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
