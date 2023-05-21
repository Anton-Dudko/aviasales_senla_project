FROM maven:3.6.3-openjdk-17-slim AS build

WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

COPY --from=build /app/target/aviasales-0.0.1-SNAPSHOT.jar /app/tripservice.jar

CMD ["java", "-jar", "/app/tripservice.jar"]

EXPOSE 3000
