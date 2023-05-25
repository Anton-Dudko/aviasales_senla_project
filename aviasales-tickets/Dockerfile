FROM openjdk:jdk-oracle

COPY target/aviasales-tickets-0.0.1-SNAPSHOT.jar /opt/aviasales-tickets.jar

CMD ["java", "-Dspring.profiles.active=cloud", "-jar", "/opt/aviasales-tickets.jar"]

EXPOSE 8088

