# -------- STAGE 1: Build --------
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# -------- STAGE 2: Run --------
FROM eclipse-temurin:17-jdk

WORKDIR /app

# ✅ Copy jar from build stage (IMPORTANT FIX)
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
