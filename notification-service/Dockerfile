FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace
COPY teachsync-events/pom.xml teachsync-events/pom.xml
COPY teachsync-events/src teachsync-events/src
RUN mvn -q -f teachsync-events/pom.xml -DskipTests install

COPY notification-service/pom.xml notification-service/pom.xml
COPY notification-service/src notification-service/src
RUN mvn -q -f notification-service/pom.xml -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /workspace/notification-service/target/*.jar /app/app.jar

EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
