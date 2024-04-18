# GatedAccess Web Service Repo

![coverage][coverage_badge]
## Before diving in 🙌

- Have a look into our [Contributing Guide](./.github/CONTRIBUTING.md)
- Our recommend IDE for this project is IntelliJ, but you can use any IDE that supports Springboot
- Request access to the `Firebase Developer Console` and `MongoDB Atlas console`
- Install the latest stable version of [Maven](https://maven.apache.org/docs/history.html). This will be your main version across your machine.

## Getting Started 🚀

This project contains 2 profiles:

- dev
- prod

To run the desired flavor either use the launch configuration in VSCode/Android Studio or use the following commands:

### Run dev profile on your machine
```sh
$  mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
### Package prod profile for deployment
```sh
# Package prod profile for deployment
$ mvn -B package --file pom.xml -DskipTests
```

## Setup MongoDB


### Springboot application.properties for production environment

Create a `application-prod.properties` file in `src/main/resources` directory and add the following variables with the appropriate values

```properties
spring.application.name=gated_access_service
spring.data.mongodb.auto-index-creation=true
spring.data.mongodb.uri=string
springdoc.api-docs.enabled=false
# Google auth
google.client.id=string
```

## Setup Google Authentication for Android (Optional)

If you want to login with Google on debug builds, then you need to add your SHA-1 debug key to [Firebase Developer Console][firebase_console_settings]

Open a terminal and run the keytool utility provided with Java to get the SHA-1 fingerprint of the certificate.


The keytool utility prompts you to enter a password for the keystore. The default password for the debug keystore is android. The keytool then prints the fingerprint to the terminal.

## Creating Routes

Routes reside in the `src/main/***/RouteController`

## Running Tests 🧪

### Unit test

### Integration test


---

## Folder Architecture 🚀

### Add
