FROM openjdk:17.0.1-jdk-slim

WORKDIR /app

COPY target/cart-service*.jar app.jar
#COPY src/main/resources/config /app/config
COPY src/main/resources/application.yml /app/config/


EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
