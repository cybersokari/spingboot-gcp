
FROM ghcr.io/graalvm/graalvm-ce:latest as build

ARG project_id
ENV GCLOUD_PROJECT=$project_id
ENV GOOGLE_APPLICATION_CREDENTIALS=/gcp/cred.json

WORKDIR /app

# Copy the  Goole Cloud credentials
COPY cred.json /gcp/cred.json

COPY .mvn/ .mvn/
COPY --chmod=0755 mvnw mvnw
COPY pom.xml .
COPY ./src src/
RUN ./mvnw dependency:go-offline -DskipTests
# Build the application
RUN ./mvnw package -Pnative -DskipTests

# Stage 2: Create the final Docker image
#FROM debian:bullseye-slim
FROM ubuntu:jammy
# Set the working directory
WORKDIR /app

# Required to run native executable on linux
RUN apt update && apt install libc6

# Copy the native executable from the build stage
COPY --from=build /app/target/web .
# Make the native executable runnable
RUN chmod 755 web
# Command to run the application
CMD ["./web"]
# Expose the port the application runs on
EXPOSE 80