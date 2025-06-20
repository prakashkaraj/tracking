# Build stage
FROM maven:3.8.4-openjdk-8 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Run stage
FROM openjdk:8-jre-slim
WORKDIR /app
COPY --from=build /app/target/tracking-0.0.1-SNAPSHOT.jar .
EXPOSE 10000
CMD ["java", "-jar", "tracking-0.0.1-SNAPSHOT.jar"]
