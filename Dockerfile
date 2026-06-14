# BƯỚC 1: Build dự án Backend Spring Boot bằng Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Chỉ copy các file cấu hình và mã nguồn Backend để tối ưu thời gian build
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src src

RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

# BƯỚC 2: Tạo bộ chạy độc lập siêu gọn nhẹ
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]