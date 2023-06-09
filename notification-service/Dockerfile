FROM maven:3.6.3-openjdk-17-slim AS maven_build
WORKDIR /tmp
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
COPY --from=maven_build /tmp/target/notification-service-0.0.1-SNAPSHOT.jar /tmp/notification-service.jar

EXPOSE 8087
CMD ["java", "-Dspring.profiles.active=cloud", "-jar", "/tmp/notification-service.jar"]
