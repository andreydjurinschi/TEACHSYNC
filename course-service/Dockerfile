FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace
COPY teachsync-events/pom.xml teachsync-events/pom.xml
COPY teachsync-events/src teachsync-events/src
RUN mvn -q -f teachsync-events/pom.xml -DskipTests install

COPY course-service/pom.xml course-service/pom.xml
COPY course-service/src course-service/src
RUN mvn -q -f course-service/pom.xml -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /workspace/course-service/target/*.jar /app/app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
