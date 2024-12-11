# Use OpenJDK 23 as the base image
FROM openjdk:23-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the target folder into the container (make sure your build step outputs to target/)
COPY target/light-message-0.0.1-SNAPSHOT.jar light-message.jar

# Expose the port the app will run on
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "light-message.jar"]
