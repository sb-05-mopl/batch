FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

COPY gradlew .
COPY gradle gradle
COPY build.gradle* settings.gradle* ./

COPY src src

RUN chmod +x gradlew \
 && ./gradlew clean bootJar -x test

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd -m appuser
USER appuser

COPY --from=build /workspace/build/libs/*.jar /app/app.jar

EXPOSE 8081

ENV TZ=Asia/Seoul

ENTRYPOINT ["java","-jar","/app/app.jar"]
