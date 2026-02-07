# 1. build the application
# use Maven image to build the project artifacts
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# copy configuration and source code
COPY pom.xml .
COPY src ./src

# build the JAR file, skipping tests (tests should be run in CI pipeline)
RUN mvn clean package -DskipTests

# 2. run the application
# use a lightweight JRE image for the final runtime container
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# expose the application port
EXPOSE 8080

# command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]