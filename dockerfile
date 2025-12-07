# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Copy application
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run as non-root user
RUN adduser -D -u 1000 spring
USER spring

# Entry point
ENTRYPOINT ["java", "-jar", "/app/app.jar"]