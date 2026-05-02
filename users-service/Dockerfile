FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace
COPY teachsync-events/pom.xml teachsync-events/pom.xml
COPY teachsync-events/src teachsync-events/src
RUN mvn -q -f teachsync-events/pom.xml -DskipTests install

COPY users-service/pom.xml users-service/pom.xml
COPY users-service/src users-service/src
RUN mvn -q -f users-service/pom.xml -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /workspace/users-service/target/*.jar /app/app.jar

EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
