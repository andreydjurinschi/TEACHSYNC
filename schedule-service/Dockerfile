FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace
COPY teachsync-events/pom.xml teachsync-events/pom.xml
COPY teachsync-events/src teachsync-events/src
RUN mvn -q -f teachsync-events/pom.xml -DskipTests install

COPY schedule-service/pom.xml schedule-service/pom.xml
COPY schedule-service/src schedule-service/src
RUN mvn -q -f schedule-service/pom.xml -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /workspace/schedule-service/target/*.jar /app/app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
