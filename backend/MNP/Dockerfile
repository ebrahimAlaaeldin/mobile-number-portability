# --- Build stage ------------------------------------------------------------
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy just the wrapper/pom first so dependency downloads are cached and skipped on
# rebuilds that only touch source code.
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

COPY src src
RUN ./mvnw -B clean package -DskipTests

# --- Runtime stage ------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Don't run the app as root inside the container.
RUN addgroup -S mnp && adduser -S mnp -G mnp
COPY --from=build /app/target/*.jar app.jar
USER mnp

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
