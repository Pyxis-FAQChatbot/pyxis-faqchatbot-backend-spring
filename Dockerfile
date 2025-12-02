# ========== 1) Builder Stage ==========
FROM gradle:8.5-jdk17 AS builder
WORKDIR /workspace

COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src ./src

RUN gradle clean build -x test --no-daemon

# ========== 2) Runtime Stage ==========
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# build 결과물 복사
COPY --from=builder /workspace/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
