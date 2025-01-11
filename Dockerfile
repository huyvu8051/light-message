# Use OpenJDK 23 as the base image
FROM openjdk:23-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the target folder into the container
COPY target/light-message-0.0.1-SNAPSHOT.jar light-message.jar

# Expose the ports for the app and JMX
EXPOSE 8080 9010

# Add JVM options for JMX and set the CMD instruction to run the app
CMD ["java", "--add-modules", "java.management,java.rmi", \
     "-Dcom.sun.management.jmxremote=true", \
     "-Dcom.sun.management.jmxremote.local.only=false", \
     "-Dcom.sun.management.jmxremote.authenticate=false", \
     "-Dcom.sun.management.jmxremote.ssl=false", \
     "-Djava.rmi.server.hostname=localhost", \
     "-Dcom.sun.management.jmxremote.port=9010", \
     "-Dcom.sun.management.jmxremote.rmi.port=9010", \
     "-jar", "light-message.jar"]
