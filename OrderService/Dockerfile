FROM openjdk:17.0.1-jdk-slim

WORKDIR /app

COPY target/orderEntity-service*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
