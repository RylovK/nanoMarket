# Используем минималистичный Java образ для запуска
FROM openjdk:17.0.1-jdk-slim

# Указываем рабочую директорию в контейнере
WORKDIR /app

# Копируем заранее собранный JAR файл в контейнер
COPY target/config-server*.jar app.jar
#COPY src/main/resources/config /app/config
COPY src/main/resources/application.yml /app/config/

# Открываем порт (если требуется)
EXPOSE 8071

# Указываем команду для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
