# Base image 설정
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사
COPY build/libs/cookingrecipe-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]