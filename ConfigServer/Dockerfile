FROM openjdk:17.0.1-jdk-slim

WORKDIR /app

COPY target/config-server*.jar app.jar
#COPY src/main/resources/config /app/config
COPY src/main/resources/application.yml /app/config/

EXPOSE 8071

ENTRYPOINT ["java", "-jar", "app.jar"]
