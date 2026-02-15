FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY target/course-service.jar course-service.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "course-service.jar"]
