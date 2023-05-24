FROM openjdk:jdk-oracle

COPY target/userservice-0.0.1-SNAPSHOT.jar /opt/userservice.jar

CMD ["java", "-Dspring.profiles.active=cloud", "-jar", "/opt/userservice.jar"]

EXPOSE 8086