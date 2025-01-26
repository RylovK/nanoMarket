FROM openjdk:17.0.1-jdk-slim

RUN apt-get update && apt-get install -y curl && apt-get clean

WORKDIR /app

COPY target/eureka-server*.jar app.jar

EXPOSE 8070

ENTRYPOINT ["java", "-jar", "app.jar"]
