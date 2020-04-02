FROM gradle:6.3-jdk11 AS build

COPY --chown=gradle:gradle . /home/gradle/tla
WORKDIR /home/gradle/tla
RUN gradle bootJar --no-daemon


FROM openjdk:11-jre-slim

RUN mkdir /app
WORKDIR /app

COPY --from=build /home/gradle/tla/build/libs/*.jar /app/tla-web-frontend.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/tla-web-frontend.jar"]
