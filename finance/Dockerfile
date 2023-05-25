FROM maven:3.6.3-openjdk-17-slim AS build

WORKDIR /app

# Копирование исходных файлов и сборка проекта
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

# Копирование Jar файла в контейнер
COPY --from=build /app/target/finance-0.0.1-SNAPSHOT.jar /app/app.jar

# Конфигурация приложения
EXPOSE 8091
CMD ["java", "-Dspring.profiles.active=cloud", "-jar", "/app/app.jar"]