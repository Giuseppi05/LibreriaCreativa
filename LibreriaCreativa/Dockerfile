# ===== Etapa 1: Construcción =====
FROM maven:3.9-eclipse-temurin-23 AS builder

WORKDIR /app

# Copiamos pom.xml y archivos de Maven para aprovechar cache
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw

# 🔹 Dar permisos de ejecución a mvnw
RUN chmod +x mvnw

# Descargamos dependencias primero
RUN ./mvnw dependency:go-offline

# Copiamos el resto del código y construimos el jar
COPY src src
RUN ./mvnw package -DskipTests

# ===== Etapa 2: Ejecución =====
FROM eclipse-temurin:23-jdk

WORKDIR /app

# Copiamos el jar generado desde la etapa de build
COPY --from=builder /app/target/LibreriaCreativa-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]