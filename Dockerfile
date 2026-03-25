# ---------- BUILD STAGE ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom first (for dependency caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests


# ---------- RUNTIME STAGE ----------
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Render uses dynamic PORT
ENV PORT=8080

# Expose port
EXPOSE 8080

# Run Spring Boot app
ENTRYPOINT ["java","-jar","app.jar","--server.port=${PORT}","--server.address=0.0.0.0"]