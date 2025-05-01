FROM gradle:8.6-jdk17 AS build
WORKDIR /app
COPY . .

# First build without tests to cache dependencies
RUN gradle build -x test --no-daemon

# Then run tests separately (with test database)
RUN gradle test --no-daemon

# Final build
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]