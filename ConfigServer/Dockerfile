FROM openjdk:17.0.1-jdk-slim

RUN apt-get update && apt-get install -y curl && apt-get clean

WORKDIR /app

COPY target/config-server*.jar app.jar
COPY src/main/resources/application.yml /app/config/

EXPOSE 8071

ENTRYPOINT ["java", "-jar", "app.jar"]
