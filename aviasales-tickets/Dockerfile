FROM maven:3.6.3-openjdk-17-slim AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:jdk-oracle

COPY  --from=build app/target/aviasales-tickets-0.0.1-SNAPSHOT.jar /opt/aviasales-tickets.jar

CMD ["java", "-Dspring.profiles.active=cloud", "-jar", "/opt/aviasales-tickets.jar"]

EXPOSE 8088

