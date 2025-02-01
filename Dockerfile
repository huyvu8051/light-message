# Stage 1: Build the Java application using Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run the application using OpenJDK 21
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copy the built JAR from the Maven build stage
COPY --from=build /app/target/light-message-0.0.1-SNAPSHOT.jar light-message.jar

# Expose application port
EXPOSE 8080 9010

# Define ENTRYPOINT to allow dynamic arguments (e.g., --spring.datasource.url=...)
ENTRYPOINT ["java", "--add-modules", "java.management,java.rmi", \
    "-Dcom.sun.management.jmxremote=true", \
    "-Dcom.sun.management.jmxremote.local.only=false", \
    "-Dcom.sun.management.jmxremote.authenticate=false", \
    "-Dcom.sun.management.jmxremote.ssl=false", \
    "-Djava.rmi.server.hostname=localhost", \
    "-Dcom.sun.management.jmxremote.port=9010", \
    "-Dcom.sun.management.jmxremote.rmi.port=9010", \
    "-jar", "light-message.jar"]

# CMD is left empty so we can pass additional parameters dynamically
CMD []
