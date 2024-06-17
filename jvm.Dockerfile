# Build jar package to run on JVM
FROM eclipse-temurin:21 as build
# GOOGLE_APPLICATION_CREDENTIALS and GCLOUD_PROJECT
# environment variables are required for a successful
# AOT compilation process, which starts the app
# before the compilation.
ARG GCLOUD_PROJECT
RUN if [ -z "$GCLOUD_PROJECT" ]; then \
     echo "PROJECT_ID is required but not set"; \
     exit 1; \
    fi
RUN echo "PROJECT_ID is set to $GCLOUD_PROJECT"
ENV GCLOUD_PROJECT=$GCLOUD_PROJECT
ENV GOOGLE_APPLICATION_CREDENTIALS=/app/credentials.json

WORKDIR /app
# Copy the  Google Cloud credentials
COPY ./credentials.json $GOOGLE_APPLICATION_CREDENTIALS
# Copy the source code
COPY .mvn/ .mvn/
COPY --chmod=0755 mvnw mvnw
COPY pom.xml .
COPY ./src src/
# Download the dependencies and cache them
RUN ./mvnw dependency:go-offline -DskipTests
# Build the application
RUN ./mvnw package -DskipTests

# Stage 2: Create the final Docker image
FROM --platform=$TARGETPLATFORM bellsoft/liberica-openjre-alpine as final
# Copy the application from the build stage
COPY --from=build /app/target/web-0.0.1-SNAPSHOT.jar .
# Command to run the application
ENTRYPOINT ["java", "-jar", "/web-0.0.1-SNAPSHOT.jar"]
# Expose the port the application runs on
EXPOSE 80