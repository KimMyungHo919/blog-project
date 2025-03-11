# 1. JDK 17을 베이스 이미지로 사용
FROM eclipse-temurin:17-jdk

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 빌드된 JAR 파일을 이미지에 복사
COPY build/libs/*.jar app.jar

# 4. 애플리케이션 실행 명령어 설정
ENTRYPOINT ["java", "-jar", "/app/app.jar"]