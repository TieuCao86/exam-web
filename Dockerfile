# BƯỚC 1: Build dự án Backend Spring Boot bằng Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# CHỈ COPY các file cấu hình cốt lõi của Java (Bỏ qua hoàn toàn folder frontend)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src src

# Cấp quyền chạy file script trên Linux
RUN chmod +x mvnw

# Tiến hành đóng gói file .jar (Ép Maven chỉ tập trung vào Backend và bỏ qua chạy Test)
RUN ./mvnw clean package -DskipTests -Dmaven.main.skip=false

# BƯỚC 2: Tạo bộ chạy độc lập siêu gọn nhẹ
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Quét và lấy file jar duy nhất được sinh ra từ bước 1
COPY --from=build /app/target/*.jar /app/app.jar

# Giới hạn bộ nhớ Heap để Spring Boot không làm sập RAM 512MB của Render
ENV JAVA_TOOL_OPTIONS="-Xmx350m -Xms256m"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]