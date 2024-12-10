# Используем базовый образ OpenJDK
FROM openjdk:17-jdk-slim as build

LABEL maintainer="Kirill Rylov <rylov.k@gmail.com>"

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем JAR-файл в образ
ARG JAR_FILE
#=target/config-server.jar
COPY ${JAR_FILE} /app/app.jar


#RUN mkdir -p target/dependency && (cd target/dependency; jar -xf /app.jar)

FROM openjdk:17-jdk-slim

VOLUME /tmp
#
#ARG DEPENDENCY=/target/dependency
#COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
#COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
#COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

COPY --from=build /app/app.jar /app/app.jar

# Указываем команду запуска
#ENTRYPOINT ["java", "-jar", "co.myproject.configserver.ConfigServerApplication.java"]
ENTRYPOINT ["java", "-jar", "/app/app.jar"]


# Указываем порт, который использует приложение
#EXPOSE 8080
