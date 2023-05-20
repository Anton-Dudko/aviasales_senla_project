FROM openjdk:jdk-oracle

LABEL maintainer="Eugene Budnichenko <thaseugene@gmail.com>"

# Set the working directory to /app
WORKDIR /app

# Copy the compiled JAR file from the host machine into the container
COPY target/*.jar /app/app.jar

# Expose port 8080 for the container
EXPOSE 8080

# Set the command to run when the container starts
ENTRYPOINT ["java", "-Dspring.profiles.active=cloud" ,"-jar", "app.jar"]