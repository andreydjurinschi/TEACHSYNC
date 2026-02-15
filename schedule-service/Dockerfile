FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY target/schedule-service.jar schedule-service.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "schedule-service.jar"]
