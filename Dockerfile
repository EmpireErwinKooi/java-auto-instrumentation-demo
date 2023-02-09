FROM gradle:7.4.2-jdk11 as build
WORKDIR /app

COPY ./src/ /app/src/
COPY build.gradle /app/
RUN gradle bootJar

FROM eclipse-temurin:17-jre
WORKDIR /app

RUN apt update && apt install tzdata -y
ENV TZ="America/Toronto"

COPY --from=build /app/build/libs/*.jar /app/spring-boot-application.jar

CMD ["java", "-jar", "/app/spring-boot-application.jar"]