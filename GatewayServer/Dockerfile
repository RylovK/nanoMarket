FROM openjdk:17.0.1-jdk-slim

RUN apt-get update && apt-get install -y curl && apt-get clean

WORKDIR /app

COPY target/gateway-server*.jar app.jar

EXPOSE 8072

ENTRYPOINT ["java", "-jar", "app.jar"]
