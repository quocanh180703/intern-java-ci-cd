# build stage
#FROM maven:3.9.9-eclipse-temurin-21 AS build
#WORKDIR /app
#COPY . .
#RUN mvn clean package -DskipTests

# run stage
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

COPY demo/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
