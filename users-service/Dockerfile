FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY target/users-service.jar users-service.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "users-service.jar"]
