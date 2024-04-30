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

To run the desired profile use `mvn spring-boot:run -P dev|prod`

## Setup MongoDB



### Springboot application.properties for production environment

Create a `application-prod.properties` file in `src/main/resources` directory and add the following variables with the appropriate values

```properties
spring.application.name=gated_access_service
spring.data.mongodb.auto-index-creation=true
spring.data.mongodb.uri=string
springdoc.api-docs.enabled=false
```

### Packaging for deployment
Run
```sh
$ mvn clean package
```
The following file are required in the `src/main/resources` directory, but are not available in Git for security purposes
1. `application-prod.properties` file for data url and production configs
2. `service-account.json` file for Firebase Admin SDK

## Creating Routes

Routes can be found in `src/main/***/http/controller`
There is a `BaseController.kt` abstract class that every `@RestController` can must inherit.

### Logging
This app uses [Logback](https://logback.qos.ch/manual) for logging. You can find the config in `/src/main/resources` folder
When running in `prod` Logs are written to a file(s) auto created by Logback in the `/opts/spring/logs` directory of the machine.
Use ``sudo chmod -R o+w /opts/spring/logs`` to grant write permission to the directory. 

Our current cloud setup uses Google's Ops Agent for collecting telemetry. You will need to install Gcloud CLI for updating the 
config when setting up a new GCP VM

#### Deploying custom config for Google's Ops Agent
Our custom Ops Agent config can be found in the ``config.yaml`` file. Use the following command to Update the Ops Agent config  when setting up a new VM
```shell 
$ gcloud compute scp config.yaml gated-vm:/etc/google-cloud-ops-agent/config.yaml
```

## Running Tests 🧪

### Unit test

### Integration test


---

## Folder Architecture 🚀