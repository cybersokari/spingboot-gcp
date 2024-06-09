FROM ubuntu:jammy
COPY target/web /web
#RUN chmod 755 web
ENTRYPOINT ["/web"]

## Stage 1: Build the Spring Boot application using GraalVM
#FROM ghcr.io/graalvm/graalvm-ce:latest as build
#
#ENV GCLOUD_PROJECT=gatedaccessdev
#ENV GOOGLE_APPLICATION_CREDENTIALS=/app/gcp/cred.json
#
## Set the working directory
#WORKDIR /app
#
## Copy the  Goole Cloud credentials
#COPY cred.json /gcp/cred.json
#
## Copy the Maven or Gradle build files
#COPY pom.xml .
#COPY --chmod=0755 mvnw mvnw
#COPY .mvn/ .mvn/
#COPY ./mvnw ./mvnw
#COPY src ./src
#
## Build the application
#RUN ./mvnw package -Pnative -DskipTests
#
## Stage 2: Create the final Docker image
#FROM debian:bullseye-slim
#
## Set the working directory
#WORKDIR /app
#
## Copy the native executable from the build stage
#COPY --from=build /app/target/web .
#
## Make the native executable runnable
#RUN chmod 755 web
#
## Command to run the application
#CMD ["./web"]
#
## Expose the port the application runs on
#EXPOSE 80