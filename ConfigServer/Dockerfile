# Используем базовый образ OpenJDK
FROM openjdk:17-jdk-slim as build

LABEL maintainer="Kirill Rylov <rylov.k@gmail.com>"

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем JAR-файл в образ
ARG JAR_FILE
COPY ${JAR_FILE} /app/app.jar

# Новый образ
FROM openjdk:17-jdk-slim

VOLUME /tmp

# Копируем скомпилированный JAR-файл из предыдущего шага
COPY --from=build /app/app.jar /app/app.jar

# Указываем команду для запуска JAR
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Если нужно, указываем порт
# EXPOSE 8080
