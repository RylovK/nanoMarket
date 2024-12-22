FROM openjdk:17.0.1-jdk-slim

WORKDIR /app

COPY target/eureka-server*.jar app.jar

EXPOSE 8070

ENTRYPOINT ["java", "-jar", "app.jar"]
