FROM openjdk:17.0.1-jdk-slim

WORKDIR /app

COPY target/product-service*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
