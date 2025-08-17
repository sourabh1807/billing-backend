# Use a lightweight JDK image
FROM eclipse-temurin:17-jdk-alpine as build

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (for caching dependencies)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application (skip tests for faster build)
RUN ./mvnw package -DskipTests

# ==============================
# Production image
# ==============================
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the fat jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Render provides PORT env var, so use it
ENV PORT=8080
EXPOSE 8080

# Run the app
ENTRYPOINT ["java","-jar","app.jar"]
