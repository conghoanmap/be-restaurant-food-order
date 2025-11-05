# Stage 1: Build
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Sử dụng JDK 17
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy file jar vào container
COPY --from=build /app/target/foodorder-0.0.1-SNAPSHOT.jar app.jar

# Cổng ứng dụng
EXPOSE 8080

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
