FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY share_recycled_stuff/pom.xml .
COPY share_recycled_stuff/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
