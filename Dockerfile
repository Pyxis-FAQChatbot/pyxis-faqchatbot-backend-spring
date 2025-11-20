# 1) 빌드 단계 (Gradle → jar 만들기)
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

# 캐시를 활용하려면 먼저 gradle 파일들을 복사
COPY build.gradle settings.gradle gradlew /app/
COPY gradle /app/gradle

RUN ./gradlew build -x test || true  # 의존성 캐싱 목적

# 본격적으로 소스 코드 복사
COPY . .

# jar 빌드
RUN ./gradlew clean build -x test

# 2) 실행 단계 (경량 JRE로 실행)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# builder 단계에서 jar 파일 가져오기
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
