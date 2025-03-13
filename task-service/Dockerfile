
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

##################################################
FROM eclipse-temurin:21
WORKDIR /app

COPY wait-for-keycloak.sh .
RUN chmod +x wait-for-keycloak.sh

COPY --from=build /app/target/task-management-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["./wait-for-keycloak.sh", "http://keycloak:8081/realms/rrom-realm/.well-known/openid-configuration", "java", "-jar", "app.jar"]
