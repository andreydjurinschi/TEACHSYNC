FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace
COPY gateway-service/pom.xml gateway-service/pom.xml
COPY gateway-service/src gateway-service/src
RUN mvn -q -f gateway-service/pom.xml -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /workspace/gateway-service/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
