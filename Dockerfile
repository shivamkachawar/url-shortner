FROM maven:3.8.5-openjdk-17 AS build
COPY . . 
RUN mvn clean package -DskipTests

# Use lightweight Java image
FROM openjdk:17.0.1-jdk-slim

# Set working directory
WORKDIR /app

# Copy jar file
COPY target/*.jar app.jar
EXPOSE 8080

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]
