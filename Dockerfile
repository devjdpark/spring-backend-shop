# 1. 자바 21 설치된 베이스 이미지 사용
FROM eclipse-temurin:21-jdk

# 2. 컨테이너 안에서 작업할 디렉토리
WORKDIR /app

# 3. 로컬에서 빌드된 JAR 파일을 이미지 안으로 복사
#    아래 파일 이름을 실제 JAR 파일 이름으로 맞춰야 한다
COPY build/libs/backend-0.0.1-SNAPSHOT.jar app.jar

# 4. 컨테이너가 실행될 때 실행할 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
