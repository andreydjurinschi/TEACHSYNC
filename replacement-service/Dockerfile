FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace
COPY teachsync-events/pom.xml teachsync-events/pom.xml
COPY teachsync-events/src teachsync-events/src
RUN mvn -q -f teachsync-events/pom.xml -DskipTests install

COPY replacement-service/pom.xml replacement-service/pom.xml
COPY replacement-service/src replacement-service/src
RUN mvn -q -f replacement-service/pom.xml -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /workspace/replacement-service/target/*.jar /app/app.jar

EXPOSE 8086
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
