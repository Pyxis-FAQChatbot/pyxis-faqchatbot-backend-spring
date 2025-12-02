# 1) Build stage
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

COPY build.gradle settings.gradle gradlew /app/
COPY gradle /app/gradle
RUN ./gradlew build -x test || true

COPY . .
RUN ./gradlew clean build -x test

# 2) Run stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# 환경변수 JAVA_OPTS 적용 필수!!!
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
