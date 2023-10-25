#Base Image
FROM eclipse-temurin:17-jdk-jammy

# Use this path as defulat location to run commands
WORKDIR /app

# copy files from local location into docker location
COPY config.yml /app
COPY pom.xml /app
# Add source code to image
COPY src /app/src

# Install Maven & dependencies
RUN apt-get update && apt-get install -y maven
# Build application with Maven
RUN mvn clean install

# What command you want to run once the image is ran in a container
CMD ["java", "-jar", "target/hellowiz-1.0-SNAPSHOT.jar", "server", "config.yml"]